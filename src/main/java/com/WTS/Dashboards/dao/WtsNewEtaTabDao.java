package com.WTS.Dashboards.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.WTS.Dashboards.Entity.WtsAppMappingTab;
import com.WTS.Dashboards.Entity.WtsAppTab;
import com.WTS.Dashboards.Entity.WtsNewEtaTab;
import com.WTS.Dashboards.Entity.WtsProcessAppMapTab;
import com.WTS.Dashboards.Entity.WtsProcessTab;
import com.WTS.Dashboards.Entity.WtsTransTab;
import com.WTS.Dashboards.Service.EmailService;
import com.WTS.Dashboards.Utility.FileCreationTime;
import com.WTS.Dashboards.Utility.TreatmentDate;

@Transactional
@Repository
public class WtsNewEtaTabDao implements IWtsDaoInterface {

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory emf;

	@Autowired
	private WtsAppTabDao appDAO;

	@Autowired
	private WtsProcessTabDao processDAO;

	@Autowired
	private EmailService emailService;

	@Autowired
	private WtsProcessAppMapTabDao proAppMapDao;

	@Autowired
	private WtsAppMappingTabDao appMapDao;

	public WtsNewEtaTab getEtaById(int newEtaId) {
		return entityManager.find(WtsNewEtaTab.class, newEtaId);
	}

	public void addNewEta(WtsNewEtaTab newEta) {

		entityManager.persist(newEta);
		entityManager.flush();

	}

	public boolean isETAExistsForProcessAndApp(int processId, int appId) {
		String hql = "FROM WtsNewEtaTab where eventDate=? and processId=? and applicationId=?";
		List<WtsNewEtaTab> ls = (List<WtsNewEtaTab>) entityManager.createQuery(hql)
				.setParameter(1, TreatmentDate.getInstance().getTreatmentDate()).setParameter(2, processId)
				.setParameter(3, appId).getResultList();
		if (!ls.isEmpty())
			return true;
		else
			return false;
	}

	public boolean isETAExistsForParentChild(int processId, int parentId, int childId) {
		String hql = "FROM WtsNewEtaTab where eventDate=? and processId=? and parentId=? and childId=?";
		List<WtsNewEtaTab> ls = (List<WtsNewEtaTab>) entityManager.createQuery(hql)
				.setParameter(1, TreatmentDate.getInstance().getTreatmentDate()).setParameter(2, processId)
				.setParameter(3, parentId).setParameter(4, childId).getResultList();
		if (!ls.isEmpty())
			return true;
		else
			return false;
	}

	public void updateNewEta(WtsNewEtaTab newEta) {
		WtsNewEtaTab eta = getEtaById(newEta.getEtaId());
		eta.setApplicationId(newEta.getApplicationId());
		eta.setProcessId(newEta.getProcessId());
		eta.setNewEtaStartTransaction(newEta.getNewEtaStartTransaction());
		eta.setNewEtaEndTransaction(newEta.getNewEtaEndTransaction());
		eta.setEventDate(TreatmentDate.getInstance().getTreatmentDate());

	}

	public void deleteNewEta(int newEtaId) {
		entityManager.remove(getEtaById(newEtaId));

	}

	
	
	public void forceInsertOrUpdateEta(WtsNewEtaTab newObj) {
		EntityManager em = emf.createEntityManager();
	    EntityTransaction tx = em.getTransaction();
	    tx.begin();
		String hql =null;
		Query qry=null;
		if(newObj.getEtaId()>0) {
			StringBuilder hqlBuilder = new StringBuilder();
			
			hqlBuilder.append("UPDATE  wts_new_eta_tab SET event_date = ?, process_id = ?,parent_id = ?,");
			hqlBuilder.append("child_id = ?,application_id = ?,");
			hqlBuilder.append("new_eta_start_transaction = ?,new_eta_end_transaction = ?,each_problem_flag =?"); 
			hqlBuilder.append(" WHERE eta_id = ?");
			qry=em.createNativeQuery(hqlBuilder.toString());
			qry.setParameter(1, newObj.getEventDate());
			qry.setParameter(2, newObj.getProcessId());
			qry.setParameter(3, newObj.getParentId());
			qry.setParameter(4, newObj.getChildId());
			qry.setParameter(5, newObj.getApplicationId());
			qry.setParameter(6, newObj.getNewEtaStartTransaction());
			qry.setParameter(7, newObj.getNewEtaEndTransaction());
			qry.setParameter(8, newObj.getProblemFlag());
			qry.setParameter(9, newObj.getEtaId());
		}else {
			StringBuilder hqlBuilder = new StringBuilder();
			hqlBuilder.append("INSERT INTO wts_new_eta_tab(event_date,process_id,parent_id,child_id,application_id,new_eta_start_transaction,new_eta_end_transaction,each_problem_flag) VALUES(");
			hqlBuilder.append("?,");
			hqlBuilder.append("?,");
			hqlBuilder.append("?,");
			hqlBuilder.append("?,");
			hqlBuilder.append("?,");
			hqlBuilder.append("?,");
			hqlBuilder.append("?,");
			hqlBuilder.append("?");
			hqlBuilder.append(")");
		
			qry=em.createNativeQuery(hqlBuilder.toString());
			qry.setParameter(1, newObj.getEventDate());
			qry.setParameter(2, newObj.getProcessId());
			qry.setParameter(3, newObj.getParentId());
			qry.setParameter(4, newObj.getChildId());
			qry.setParameter(5, newObj.getApplicationId());
			qry.setParameter(6, newObj.getNewEtaStartTransaction());
			qry.setParameter(7, newObj.getNewEtaEndTransaction());
			qry.setParameter(8, newObj.getProblemFlag());
			
			
		}
		
		if(qry!=null) {
			qry.executeUpdate();
		}
		 tx.commit();
		 em.close();
	}
	
	public int isProblemFlag() {
		int flag = 0;
		String hql = "select problemFlag FROM WtsNewEtaTab where eventDate= current_date";
		int entry = entityManager.createQuery(hql).getResultList().size();
		if (entry != 0) {
			flag = 0;
		} else {
			flag = 1;
		}
		return flag;
	}

	
	
	
	/*public void newEtaCalculation(WtsAppTab app, int processId) throws ParseException {
		System.out.println("start time loop entered");
		String name = app.getName();
		int apId = app.getApplicationId();

		List<WtsNewEtaTab> etaLst = new ArrayList<>();
		Timestamp startDTTime = null;
		Timestamp endDTTime = null;
		int buf = 0;
		int currentSeq = 0;
		WtsProcessAppMapTab proMap = proAppMapDao.getAllAppMappingsByProcess(processId, apId);
		if (proMap != null) {
			startDTTime = proMap.getStartTime();
			endDTTime = proMap.getEndTime();
			buf = proMap.getBufferTime();
			currentSeq = proMap.getSequence();
		}

		Long origDiff = Long.valueOf(0);
		origDiff = endDTTime.getTime() - startDTTime.getTime();

		Timestamp fileStart = null;
		Timestamp fileEnd = null;
		Timestamp lastEnd = null;
		Long endDiff = Long.valueOf(0);
		Long startDiff = Long.valueOf(0);

		if (FileCreationTime.getStartfileCreationTime(name) != null) {
			fileStart = FileCreationTime.startTimestamp(name);
			startDiff = (fileStart.getTime() - startDTTime.getTime());
		}
		if (FileCreationTime.getEndfileCreationTime(name) != null) {
			fileEnd = FileCreationTime.endTimestamp(name);
			endDiff = (fileEnd.getTime() - endDTTime.getTime());
		}
		WtsProcessTab processObj = processDAO.getProcessById(processId);
		Timestamp startProcessTime = null;
		Timestamp endProcessTime = null;

		if (processObj != null) {
			startProcessTime = processObj.getExpectedStartTime();
			endProcessTime = processObj.getExpectedEndTime();
		}

		List<WtsProcessAppMapTab> apps = proAppMapDao.getAllAppMappingsForProcess(processId);

		
		if (startDiff > 0) {
			this.updateDifferencePROCESSETA(startDiff, origDiff, etaLst, apps, startProcessTime, endProcessTime,
					fileStart.getTime(), processId, false,currentSeq);
		}
		if (endDiff > 0) {
			this.updateDifferencePROCESSETA(endDiff, origDiff, etaLst, apps, startProcessTime, endProcessTime,
					fileStart.getTime(), processId, true,currentSeq);

		}

		
		
		entityManager.flush();

	}
*/
	public void newEtaCalculationForChilds(int parentId, int childId, int processId, boolean mainpageNav)
			throws ParseException {
		System.out.println("start time loop entered");

		WtsAppTab parentApp = appDAO.getAppById(parentId);
		WtsAppTab childApp = appDAO.getAppById(childId);

		String name = childApp.getName();

		
		Timestamp startDTTime = null;
		Timestamp endDTTime = null;
		int buf = 0;
		int currentSeq = 0;
		WtsAppMappingTab proMap = appMapDao.getAppMappingsByParent(parentId, childId, processId);
		if (proMap != null) {
			startDTTime = proMap.getStartTime();
			endDTTime = proMap.getEndTime();
			buf = proMap.getBufferTime();
			currentSeq = proMap.getSequence();
		}

		
		// problem area doubt for parent ETA update-RED BLOCK
				Timestamp startProcessTime = null;
				Timestamp endProcessTime = null;
				WtsNewEtaTab  oldETA=getTdyETATxnByParentChildID(parentId, childId,
						processId, TreatmentDate.getInstance().getTreatmentDate());
				if (!mainpageNav) {
					WtsAppMappingTab parentMap = appMapDao.getAppMappingsByParent(parentId, childId, processId);
					if (parentMap != null) {
						startProcessTime = parentMap.getStartTime();
						endProcessTime = parentMap.getEndTime();
					}
					
					
				} else {
					WtsProcessAppMapTab parentMap = proAppMapDao.getAllAppMappingsByProcess(processId, parentId);
					if (parentMap != null) {
						startProcessTime = parentMap.getStartTime();
						endProcessTime = parentMap.getEndTime();
					}
					
					
				}
				
				if(oldETA!=null) {
					startDTTime = oldETA.getNewEtaStartTransaction();
					endDTTime = oldETA.getNewEtaEndTransaction();
				}
		Long origDiff = Long.valueOf(0);
		origDiff = endDTTime.getTime() - startDTTime.getTime();

		Timestamp fileStart = null;
		Timestamp fileEnd = null;
		Timestamp lastEnd = null;
		Long endDiff = Long.valueOf(0);
		Long startDiff = Long.valueOf(0);

		if (FileCreationTime.getStartfileCreationTime(name) != null) {
			fileStart = FileCreationTime.startTimestamp(name);
			startDiff = (fileStart.getTime() - startDTTime.getTime());
		}
		if (FileCreationTime.getEndfileCreationTime(name) != null) {
			fileEnd = FileCreationTime.endTimestamp(name);
			endDiff = (fileEnd.getTime() - endDTTime.getTime());
		}

		
		List<WtsAppMappingTab> apps = appMapDao.getAllAppMappingsByParent(parentId, processId);

		// start Change case
		
		if (endDiff != 0) {
			this.updateDifferenceETA(endDiff, origDiff, apps, startProcessTime, endProcessTime,
					fileStart.getTime(), parentId, processId, true,currentSeq);

		} if (startDiff != 0 ) {
			this.updateDifferenceETA(startDiff, origDiff, apps, startProcessTime, endProcessTime,
					fileStart.getTime(), parentId, processId, false,currentSeq);

		}
		entityManager.flush();
		System.out.println("ETA set for start transations..");
	}
	@Transactional
	public void updateDifferenceETA(Long diff, Long origDiff, List<WtsAppMappingTab> apps,
			Timestamp startParentTime, Timestamp endParentTime, Long fileStartTime, int parentId,
			int processId, boolean endFlow,int currentSeq) {

		Iterator<WtsAppMappingTab> apIt = apps.iterator();
		int buf = 0;
		boolean persist=false;

		while (apIt.hasNext()) {
			WtsAppMappingTab nextApp = (WtsAppMappingTab) apIt.next();

			int nextAppSeq = 0;
			Timestamp startApp = null;
			Timestamp endDtTime = null;
			Timestamp origstart = null;
			Timestamp origend = null;
			if (nextApp != null) {
				startApp = nextApp.getStartTime();
				endDtTime = nextApp.getEndTime();
				origstart = nextApp.getStartTime();
				origend = nextApp.getEndTime();
				buf = nextApp.getBufferTime();
				nextAppSeq = nextApp.getSequence();
			}
			
			
			
			
			if (nextAppSeq == currentSeq) {
				WtsNewEtaTab curObj = getTdyETATxnByParentChildID(nextApp.getParentId(), nextApp.getChildId(),
						nextApp.getProcessId(), TreatmentDate.getInstance().getTreatmentDate());
				if (curObj == null) {
					curObj = new WtsNewEtaTab();
					persist=true;
				}
				WtsNewEtaTab  oldETA=getTdyETATxnByParentChildID(nextApp.getParentId(), nextApp.getChildId(),
						nextApp.getProcessId(), TreatmentDate.getInstance().getTreatmentDate());
				if(oldETA!=null) {
					startApp = oldETA.getNewEtaStartTransaction();
					endDtTime = oldETA.getNewEtaEndTransaction();
				}
				Timestamp newEnd = null;
				Timestamp newStart = null;
				if (!endFlow) {
					Long newStartL = fileStartTime;
					newStart = new Timestamp(newStartL);
					Long newEndL = origDiff + newStartL;
					newEnd = new Timestamp(newEndL);
					endParentTime = new Timestamp(diff + endParentTime.getTime());
				} else {
					 
					Long newStartL = fileStartTime;
					newStart = new Timestamp(newStartL);
					 if(diff<0){
						 if(oldETA!=null && startApp!=null){
							 newStartL = startApp.getTime();
							newStart = new Timestamp(newStartL);
						 }
						  Long newEndL = origDiff + newStartL;
						  newEnd = new Timestamp(newEndL);
					  }else{
					Long newEndL = diff + endDtTime.getTime();
					newEnd = new Timestamp(newEndL);
					  }
					endParentTime = new Timestamp(diff + endParentTime.getTime());
				}
				if(origstart.after(newStart))
				{
					newStart=origstart;
				}
				if(origend.after(newEnd))
				{
					newEnd=origend;
				}
				curObj.setNewEtaEndTransaction(newEnd);
				
				curObj.setNewEtaStartTransaction(newStart);
				if(persist) {
				curObj.setParentId(nextApp.getParentId());
				curObj.setChildId(nextApp.getChildId());
				}
				curObj.setProcessId(nextApp.getProcessId());
				curObj.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
				curObj.setProblemFlag(1);
		
			    
				this.forceInsertOrUpdateEta(curObj);
				
				
				prepareParentETAs(nextApp.getParentId(),nextApp.getProcessId(),nextApp.getChildId(),currentSeq,diff,endFlow,fileStartTime);
				prepareChildETAs(nextApp.getParentId(),nextApp.getProcessId(),nextApp.getChildId(),currentSeq,diff,endFlow,fileStartTime);
				entityManager.flush();
			}

		}
	

	}

	@Transactional
	private void prepareChildETAs(int parentId, int processId, int childId, int currentSeq, Long diff, boolean endFlow, Long fileStartTime) {
		
		//immidiate below children
		List<WtsAppMappingTab> immChildMappings= appMapDao.getImmediateChildAppMappings(parentId, processId, currentSeq);
		if(immChildMappings!=null && !immChildMappings.isEmpty()) {
			Iterator<WtsAppMappingTab> chlItr=immChildMappings.iterator();
			while (chlItr.hasNext()) {
				boolean persist=false;
				WtsAppMappingTab wtsAppMappingTab = (WtsAppMappingTab) chlItr.next();
				Timestamp oldstartTime = null;
				Timestamp oldendDtTime = null;
				Timestamp origstart = null;
				Timestamp origend = null;
				Long origDiff = Long.valueOf(0);
				if (wtsAppMappingTab != null) {
					oldstartTime = wtsAppMappingTab.getStartTime();
					oldendDtTime = wtsAppMappingTab.getEndTime();
					origstart = wtsAppMappingTab.getStartTime();
					origend = wtsAppMappingTab.getEndTime();
					origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
				}
				
				WtsNewEtaTab et = getTdyETATxnByParentChildID(parentId,wtsAppMappingTab.getChildId(), processId, TreatmentDate.getInstance().getTreatmentDate());
				if (et == null) {
					et = new WtsNewEtaTab();
					persist=true;
				}else {
					oldstartTime = et.getNewEtaStartTransaction();
					oldendDtTime = et.getNewEtaEndTransaction();
					origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
				}
				//early finish
				if(diff<0) {
					origDiff =diff;
				}
				Timestamp newEnd = null;
				Timestamp newStart = null;
				if (!endFlow) {
					Long newStartL = diff +oldstartTime.getTime();
					newStart = new Timestamp(newStartL);
					Long newEndL = origDiff + newStartL;
					newEnd = new Timestamp(newEndL);
				} 
				
				else {
					Long newStartL =oldstartTime.getTime()+diff;
					newStart = new Timestamp(newStartL);
					Long newEndL = diff + oldendDtTime.getTime();
					newEnd = new Timestamp(newEndL);
				}
				if(origstart.after(newStart))
				{
					newStart=origstart;
				}
				if(origend.after(newEnd))
				{
					newEnd=origend;
				}
				et.setNewEtaEndTransaction(newEnd);
				et.setNewEtaStartTransaction(newStart);
				et.setParentId(parentId);
				et.setChildId(wtsAppMappingTab.getChildId());
				et.setProcessId(processId);
				et.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
				et.setProblemFlag(1);
				this.forceInsertOrUpdateEta(et);
				entityManager.flush();			
			}
		}
		
		//Parent siblings
		List<WtsProcessAppMapTab> procHigherMapping=proAppMapDao.getAllHigherAppMappingsByProcess(processId, parentId);
		if(procHigherMapping!=null && !procHigherMapping.isEmpty()) {
			Iterator<WtsProcessAppMapTab> proItr=procHigherMapping.iterator();
			while (proItr.hasNext()) {
				boolean persist=false;
				WtsProcessAppMapTab wtsProcessAppMapTab = (WtsProcessAppMapTab) proItr.next();
				Timestamp oldstartTime = null;
				Timestamp oldendDtTime = null;
				Timestamp origstart = null;
				Timestamp origend = null;
				Long origDiff = Long.valueOf(0);
				if (wtsProcessAppMapTab != null) {
					oldstartTime = wtsProcessAppMapTab.getStartTime();
					oldendDtTime = wtsProcessAppMapTab.getEndTime();
					origstart = wtsProcessAppMapTab.getStartTime();
					origend = wtsProcessAppMapTab.getEndTime();
					origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
				}
				
				WtsNewEtaTab et = getTdyETATxnByParentId(wtsProcessAppMapTab.getApplicationId(), processId, TreatmentDate.getInstance().getTreatmentDate());
				if (et == null) {
					et = new WtsNewEtaTab();
					persist=true;
				}else {
					oldstartTime = et.getNewEtaStartTransaction();
					oldendDtTime = et.getNewEtaEndTransaction();
					origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
				}
				//early finish
				Timestamp newEnd = null;
				Timestamp newStart = null;
				if (!endFlow) {
					Long newStartL = diff +oldstartTime.getTime();
					newStart = new Timestamp(newStartL);
					Long newEndL = origDiff + newStartL;
					newEnd = new Timestamp(newEndL);
				}
				else {
					Long newStartL =oldstartTime.getTime()+diff;
					newStart = new Timestamp(newStartL);
					Long newEndL = diff + oldendDtTime.getTime();
					newEnd = new Timestamp(newEndL);
				}
				if(origstart.after(newStart))
				{
					newStart=origstart;
				}
				if(origend.after(newEnd))
				{
					newEnd=origend;
				}
				et.setNewEtaEndTransaction(newEnd);
				et.setNewEtaStartTransaction(newStart);
				et.setParentId(wtsProcessAppMapTab.getApplicationId());
				et.setProcessId(processId);
				et.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
				et.setProblemFlag(1);
				this.forceInsertOrUpdateEta(et);
				persist=false;
				//update equivalent app ETA record..
				WtsNewEtaTab topET = getTdyETATxnByProcessIdAppID(wtsProcessAppMapTab.getApplicationId(), processId,  TreatmentDate.getInstance().getTreatmentDate());
				if (topET == null) {
					topET = new WtsNewEtaTab();
					persist=true;
				}
				topET.setNewEtaEndTransaction(newEnd);
				topET.setNewEtaStartTransaction(newStart);
				topET.setApplicationId(wtsProcessAppMapTab.getApplicationId());
				topET.setProcessId(processId);
				topET.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
				topET.setProblemFlag(1);
				this.forceInsertOrUpdateEta(topET);
				
				//fetch all childs of it and update
				List<WtsAppMappingTab> siblingChildMappings=appMapDao.getAllAppMappingsByParent(wtsProcessAppMapTab.getApplicationId(), processId);
				if(siblingChildMappings!=null && !siblingChildMappings.isEmpty()) {
					Iterator<WtsAppMappingTab> sibItr=siblingChildMappings.iterator();
					while (sibItr.hasNext()) {
						WtsAppMappingTab wtsAppMappingTab = (WtsAppMappingTab) sibItr.next();
						persist=false;
						
						if (wtsAppMappingTab != null) {
							oldstartTime = wtsAppMappingTab.getStartTime();
							oldendDtTime = wtsAppMappingTab.getEndTime();
							origstart = wtsAppMappingTab.getStartTime();
							origend = wtsAppMappingTab.getEndTime();
							origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
						}
						
						WtsNewEtaTab childETA = getTdyETATxnByParentChildID(wtsAppMappingTab.getParentId(),wtsAppMappingTab.getChildId(), processId, TreatmentDate.getInstance().getTreatmentDate());
						if (childETA == null) {
							childETA = new WtsNewEtaTab();
							persist=true;
						}else {
							oldstartTime = childETA.getNewEtaStartTransaction();
							oldendDtTime = childETA.getNewEtaEndTransaction();
							origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
						}
						//early finish
						
						if (!endFlow) {
							Long newStartL = diff +oldstartTime.getTime();
							newStart = new Timestamp(newStartL);
							Long newEndL = origDiff + newStartL;
							newEnd = new Timestamp(newEndL);
						} else {
							Long newStartL =oldstartTime.getTime();
							newStart = new Timestamp(newStartL);
							Long newEndL = diff + oldendDtTime.getTime();
							newEnd = new Timestamp(newEndL);
						}
						if(origstart.after(newStart))
						{
							newStart=origstart;
						}
						if(origend.after(newEnd))
						{
							newEnd=origend;
						}
						
						childETA.setNewEtaEndTransaction(newEnd);
						childETA.setNewEtaStartTransaction(newStart);
						childETA.setParentId(wtsAppMappingTab.getParentId());
						childETA.setChildId(wtsAppMappingTab.getChildId());
						childETA.setProcessId(processId);
						childETA.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
						childETA.setProblemFlag(1);
						this.forceInsertOrUpdateEta(childETA);
					}
				}
				
			}
		}
		
		
	}

	
	
	@Transactional
	private void prepareParentETAs(int parentId, int processId, int childId, int currentSeq, Long diff, boolean endFlow, Long fileStartTime) {
		
		List<WtsProcessAppMapTab> procHigherMapping=proAppMapDao.getAllLowerAppMappingsByProcess(processId, parentId);
		if(procHigherMapping!=null && !procHigherMapping.isEmpty()) {
			Iterator<WtsProcessAppMapTab> proItr=procHigherMapping.iterator();
			while (proItr.hasNext()) {
				boolean persist=false;
				WtsProcessAppMapTab wtsProcessAppMapTab = (WtsProcessAppMapTab) proItr.next();
				Timestamp oldstartTime = null;
				Timestamp oldendDtTime = null;
				Timestamp origstart = null;
				Timestamp origend = null;
				Long origDiff = Long.valueOf(0);
				if (wtsProcessAppMapTab != null) {
					oldstartTime = wtsProcessAppMapTab.getStartTime();
					oldendDtTime = wtsProcessAppMapTab.getEndTime();
					origstart = wtsProcessAppMapTab.getStartTime();
					origend = wtsProcessAppMapTab.getEndTime();
					origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
				}
				
				WtsNewEtaTab et = getTdyETATxnByParentId(wtsProcessAppMapTab.getApplicationId(), processId, TreatmentDate.getInstance().getTreatmentDate());
				if (et == null) {
					et = new WtsNewEtaTab();
					persist=true;
				}else {
					oldstartTime = et.getNewEtaStartTransaction();
					oldendDtTime = et.getNewEtaEndTransaction();
					origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
				}
				//early finish
				if(diff<0) {
					origDiff =diff;
				}
				Timestamp newEnd = null;
				Timestamp newStart = null;
				Long newStartL = null;
				if (!endFlow) {
						if(currentSeq==1){
							 newStartL = oldstartTime.getTime()+diff;
						}
						else{
							 newStartL=oldstartTime.getTime();
						}
						
					if(wtsProcessAppMapTab.getApplicationId()!=parentId)
						newStartL = oldstartTime.getTime();
						newStart = new Timestamp(newStartL);
						Long newEndL = origDiff + newStartL;
						newEnd = new Timestamp(newEndL);
				} 
			else {
					 newStartL = oldstartTime.getTime();
					if(wtsProcessAppMapTab.getApplicationId()!=parentId)
						newStartL = oldstartTime.getTime();
					newStart = new Timestamp(newStartL);
					Long newEndL =diff+oldendDtTime.getTime();
					newEnd = new Timestamp(newEndL);
				}
				if(origstart.after(newStart))
				{
					newStart=origstart;
				}
				if(origend.after(newEnd))
				{
					newEnd=origend;
				}
				et.setNewEtaEndTransaction(newEnd);
				et.setNewEtaStartTransaction(newStart);
				et.setParentId(wtsProcessAppMapTab.getApplicationId());
				et.setProcessId(processId);
				et.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
				et.setProblemFlag(1);
				this.forceInsertOrUpdateEta(et);
				 persist=false;
				WtsNewEtaTab topET = getTdyETATxnByProcessIdAppID(wtsProcessAppMapTab.getApplicationId(), processId,  TreatmentDate.getInstance().getTreatmentDate());
				if (topET == null) {
					topET = new WtsNewEtaTab();
					persist=true;
				}
				topET.setNewEtaEndTransaction(newEnd);
				topET.setNewEtaStartTransaction(newStart);
				topET.setApplicationId(wtsProcessAppMapTab.getApplicationId());
				topET.setProcessId(processId);
				topET.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
				topET.setProblemFlag(1);
				this.forceInsertOrUpdateEta(topET);
				
			}		
		}
		
		boolean  persist=false;
		Timestamp newEnd = null;
		Timestamp newStart = null;
		Timestamp oldstartTime = null;
		Timestamp oldendDtTime = null;
		Timestamp origstart = null;
		Timestamp origend = null;
		WtsProcessTab procObj=processDAO.getProcessById(processId);
		Long origDiff = Long.valueOf(0);
		
		if (procObj != null) {
			oldstartTime = procObj.getExpectedStartTime();
			oldendDtTime = procObj.getExpectedEndTime();
			origstart = procObj.getExpectedStartTime();
			origend = procObj.getExpectedEndTime();
			origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
		}
		WtsNewEtaTab procET = getTdyETATxnByProcessId(processId,  TreatmentDate.getInstance().getTreatmentDate());
		if (procET == null) {
			procET = new WtsNewEtaTab();
			persist=true;
		}else {
			oldstartTime = procET.getNewEtaStartTransaction();
			oldendDtTime = procET.getNewEtaEndTransaction();
			origDiff = oldendDtTime.getTime() - oldstartTime.getTime();
		}
		//early finish
		if(diff<0) {
			origDiff =diff;
		}
		if (!endFlow) {
			Long newStartL = oldstartTime.getTime()+diff;
			newStart = new Timestamp(newStartL);
			Long newEndL = diff+oldendDtTime.getTime();
			newEnd = new Timestamp(newEndL);
		} else {
			Long newStartL = oldstartTime.getTime()+diff;
			newStart = new Timestamp(newStartL);
			Long newEndL =diff+oldendDtTime.getTime();
			newEnd = new Timestamp(newEndL);
		}
		if(origstart.after(newStart))
		{
			newStart=origstart;
		}
		if(origend.after(newEnd))
		{
			newEnd=origend;
		}
		procET.setNewEtaEndTransaction(newEnd);
		procET.setNewEtaStartTransaction(oldstartTime);
		procET.setParentId(0);
		procET.setChildId(0);
		procET.setApplicationId(0);
		procET.setProcessId(processId);
		procET.setEventDate(TreatmentDate.getInstance().getTreatmentDate());
		procET.setProblemFlag(1);
		this.forceInsertOrUpdateEta(procET);
		entityManager.flush();

	}
	
	
	public List<WtsNewEtaTab> etaListUpdateWithNoDuplicate(List<WtsNewEtaTab> etaLst, WtsNewEtaTab newObj) {
		boolean duplicate=false;
		if(etaLst!=null) {
			Iterator<WtsNewEtaTab> lstItr=etaLst.iterator();
			while (lstItr.hasNext()) {
				WtsNewEtaTab wtsNewEtaTab = (WtsNewEtaTab) lstItr.next();
				if(wtsNewEtaTab.getApplicationId()== newObj.getApplicationId() &&
						wtsNewEtaTab.getProcessId()== newObj.getProcessId() &&
						wtsNewEtaTab.getParentId()== newObj.getParentId() &&
						wtsNewEtaTab.getChildId()== newObj.getChildId() &&
						(wtsNewEtaTab.getEventDate()!=null && wtsNewEtaTab.getEventDate().equalsIgnoreCase(newObj.getEventDate() ))) {
						if(wtsNewEtaTab.getApplicationId()==0 && wtsNewEtaTab.getParentId()==0 && wtsNewEtaTab.getChildId()==0)
						{
							System.out.println("process duplicate..no update");
						}else {
							wtsNewEtaTab.setNewEtaEndTransaction(newObj.getNewEtaEndTransaction());
							wtsNewEtaTab.setNewEtaStartTransaction(newObj.getNewEtaStartTransaction());
							wtsNewEtaTab.setProblemFlag(newObj.getProblemFlag());
						}		
						duplicate=true;
						break;
				}
			}
			if(!duplicate) {
				etaLst.add(newObj);
			}
		}
		
		return etaLst;
	}

	public WtsNewEtaTab getTdyETATxnByProcessId(int processId, String treatmentDate) {
		String hql = "from WtsNewEtaTab WHERE processId=?  AND eventDate= ? and (applicationId=0 or applicationId IS NULL) and (parentId=0  or parentId IS NULL)and (childId=0  or childId IS NULL)";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processId);
		qry.setParameter(2, treatmentDate);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsNewEtaTab) qry.getResultList().get(0);
		else
			return null;

	}

	private WtsNewEtaTab getTdyETATxnByParentId(int parentId, int processId, String treatmentDate) {
		String hql = "from WtsNewEtaTab WHERE processId=?  AND eventDate= ?  AND parentId=? and applicationId=0 and childId=0";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processId);
		qry.setParameter(2, treatmentDate);
		qry.setParameter(3, parentId);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsNewEtaTab) qry.getResultList().get(0);
		else
			return null;

	}

	public void updateGreenDay(int processId, String treatmentDate) {
		String hql = "UPDATE WtsNewEtaTab SET problemFlag= 0 WHERE processId=?  AND eventDate= ?";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processId);
		qry.setParameter(2, treatmentDate);
		qry.executeUpdate();
	}

	public void updateGreenDayForChilds(int processId, int parentId, String treatmentDate) {
		String hql = "UPDATE WtsNewEtaTab SET problemFlag= 0 WHERE processId=?  AND eventDate= ? AND parentId=?";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processId);
		qry.setParameter(2, treatmentDate);
		qry.setParameter(3, parentId);
		qry.executeUpdate();
	}

	public WtsNewEtaTab getTdyETATxnByProcessIdAppID(int appId, int processid, String treatDt) {

		String hql = "from WtsNewEtaTab WHERE processId=? AND applicationId=? AND eventDate= ? AND parentId=0 and childId=0";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processid);
		qry.setParameter(2, appId);
		qry.setParameter(3, treatDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsNewEtaTab) qry.getResultList().get(0);
		else
			return null;

	}

	public WtsNewEtaTab getTdyETATxnByParentChildID(int parentId, int childId, int processid, String treatDt) {

		String hql = "from WtsNewEtaTab WHERE processId=? AND parentId=? AND childId=? AND eventDate= ?";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processid);
		qry.setParameter(2, parentId);
		qry.setParameter(3, childId);
		qry.setParameter(4, treatDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsNewEtaTab) qry.getResultList().get(0);
		else
			return null;

	}

	public List<WtsNewEtaTab> getEtaByAppIdProcessId(int processId, int appId) {
		try {
			String hql = "From WtsNewEtaTab where applicationId=? and processId=? and eventDate=?";
			List<WtsNewEtaTab> et = (List<WtsNewEtaTab>) entityManager.createQuery(hql).setParameter(1, appId)
					.setParameter(2, processId).setParameter(3, TreatmentDate.getInstance().getTreatmentDate())
					.getSingleResult();
			return et;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<WtsNewEtaTab> getAllEta() {
		String hql = "FROM WtsNewEtaTab as et where et.eventDate=?  AND et.parentId IS NULL AND et.childId IS NULL ORDER BY et.applicationId";
		return (List<WtsNewEtaTab>) entityManager.createQuery(hql)
				.setParameter(1, TreatmentDate.getInstance().getTreatmentDate()).getResultList();
	}

	public List<WtsNewEtaTab> getAllChildEta(int parentId, int processId) {
		String hql = "FROM WtsNewEtaTab as et where et.eventDate=? AND et.processId=? AND et.parentId=? ORDER BY et.childId";
		return (List<WtsNewEtaTab>) entityManager.createQuery(hql)
				.setParameter(1, TreatmentDate.getInstance().getTreatmentDate()).setParameter(2, processId)
				.setParameter(3, parentId).getResultList();
	}

	public List<WtsNewEtaTab> getAllCurrentEta() {
		String hql = "FROM WtsNewEtaTab where eventDate=?";
		return (List<WtsNewEtaTab>) entityManager.createQuery(hql)
				.setParameter(1, TreatmentDate.getInstance().getTreatmentDate()).getResultList();
	}

}
