package com.WTS.Dashboards.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.WTS.Dashboards.Controller.WtsTransTabController;
import com.WTS.Dashboards.Entity.WtsAppMappingTab;
import com.WTS.Dashboards.Entity.WtsAppTab;
import com.WTS.Dashboards.Entity.WtsNewEtaTab;
import com.WTS.Dashboards.Entity.WtsProcessAppMapTab;
import com.WTS.Dashboards.Entity.WtsProcessTab;
import com.WTS.Dashboards.Entity.WtsTransTab;
import com.WTS.Dashboards.Service.EmailService;
//import com.WTS.Dashboards.Service.EmailService;
import com.WTS.Dashboards.Utility.DateUtility;
import com.WTS.Dashboards.Utility.FileCreationTime;
import com.WTS.Dashboards.Utility.TreatmentDate;

@Transactional
@Repository
public class WtsTransTabDao implements IWtsDaoInterface {

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private WtsProcessTabDao processDAO;

	@Autowired
	private WtsAppTabDao appDAO;

	@Autowired
	private WtsNewEtaTabDao etDAO;
	@Autowired
	private EmailService emailService;
	@Autowired
	private WtsProcessAppMapTabDao proAppMapDao;
	
	@Autowired
	private WtsAppMappingTabDao appMapDao;
	
	int prevTempStatus=WtsTransTabController.STATUS_YET_TO_START;

	public WtsTransTab getTransactionById(int transactionId) {
		return entityManager.find(WtsTransTab.class, transactionId);
	}

	@SuppressWarnings("unchecked")

	public List<WtsTransTab> getAlltransaction() {
		String hql = "FROM WtsTransTab as tran ORDER BY tran.transactionId";
		return (List<WtsTransTab>) entityManager.createQuery(hql).getResultList();
	}

	public void addTransaction(WtsTransTab transaction, String name) {
		try {
			transaction.setEventDate(TreatmentDate.getInstance().getTreatmentDate());

			if (FileCreationTime.getStartfileCreationTime(name) != null)
				transaction.setStartTransaction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(FileCreationTime.getStartfileCreationTime(name)));
			if (FileCreationTime.getEndfileCreationTime(name) != null)
				transaction.setEndTransaction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(FileCreationTime.getEndfileCreationTime(name)));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		entityManager.persist(transaction);
		entityManager.flush();

	}

	public void updateTransaction(WtsTransTab transaction) {
		WtsTransTab tran = getTransactionById(transaction.getTransactionId());
		System.out.println("Check---------> by transactionID");
		tran.setEventDate(transaction.getEventDate());
		tran.setStatusId(transaction.getStatusId());
		entityManager.flush();
	}

	public void deleteTransaction(int transactionId) {
		entityManager.remove(getTransactionById(transactionId));
	}

	public WtsTransTab getTdyTxnByProcessId(int processId, String trtDt) {
		String hql = "from WtsTransTab WHERE processId=? and eventDate= ? AND application_id IS null and parent_id IS null";

		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processId);
		qry.setParameter(2, trtDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsTransTab) qry.getResultList().get(0);
		else
			return null;

	}
	
	public List<WtsTransTab> getAllTdyTxnByProcessId(int processId, String trtDt) {
		String hql = "from WtsTransTab WHERE processId=? and eventDate= ?";

		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processId);
		qry.setParameter(2, trtDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return qry.getResultList();
		else
			return null;

	}

	public WtsTransTab getTdyTxnByParentId(int parentId, int processId, String trtDt) {
		String hql = "from WtsTransTab tab WHERE tab.parentId=? and tab.processId=? and eventDate= ?";

		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, parentId);
		qry.setParameter(2, processId);
		qry.setParameter(3, trtDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsTransTab) qry.getResultList().get(0);
		else
			return null;

	}
	
	public List<WtsTransTab> getAllTdyTxnByParentId(int parentId, int processId, String trtDt) {
		String hql = "from WtsTransTab tab WHERE tab.parentId=? and tab.processId=? and eventDate= ? and childId is not null";

		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, parentId);
		qry.setParameter(2, processId);
		qry.setParameter(3, trtDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return qry.getResultList();
		else
			return null;

	}
	
	public WtsTransTab getTransactionByAppIdProId(int appId, int processid, String treatDt) {
		String hql = "from WtsTransTab WHERE processId=? AND applicationId=? AND eventDate= ?";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processid);
		qry.setParameter(2, appId);
		qry.setParameter(3, treatDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsTransTab) qry.getResultList().get(0);
		else
			return null;

	}
	
	public WtsTransTab getTransactionByParentChildId(int childId, int parentId, int processId, String treatDt) {
		String hql = "from WtsTransTab WHERE parentId=? AND childId=? AND eventDate= ? AND processId=?";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, parentId);
		qry.setParameter(2, childId);
		qry.setParameter(3, treatDt);
		qry.setParameter(4, processId);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsTransTab) qry.getResultList().get(0);
		else
			return null;

	}
	
	public WtsTransTab getTransactionByParentChildId(int parentId, int processId, String treatDt) {
		String hql = "from WtsTransTab WHERE parentId=? AND eventDate= ? AND processId=? AND childId=0";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, parentId);
		qry.setParameter(2, treatDt);
		qry.setParameter(3, processId);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsTransTab) qry.getResultList().get(0);
		else
			return null;

	}

	public WtsTransTab getTransactionByAppIdProIdBatId(int appId, int processid, int batchId, Date treatDt) {
		String hql = "from WtsTransTab WHERE processId=? AND applicationId=? AND batchId=? AND eventDate= ?";
		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processid);
		qry.setParameter(2, appId);
		qry.setParameter(3, batchId);
		qry.setParameter(4, treatDt);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsTransTab) qry.getResultList().get(0);
		else
			return null;

	}

	public WtsTransTab getTdyTxnByProcessIdAppId(int processId, String trtDt, int appId) {
		String hql = "from WtsTransTab WHERE processId=? and eventDate= ? AND application_id= ? and parent_id IS null and childId IS NULL";

		Query qry = entityManager.createQuery(hql);
		qry.setParameter(1, processId);
		qry.setParameter(2, trtDt);
		qry.setParameter(3, appId);
		if (qry.getResultList() != null && !qry.getResultList().isEmpty())
			return (WtsTransTab) qry.getResultList().get(0);
		else
			return null;

	}

	public boolean transactionExistsbyProcessId(int process) {
		System.out.println("boolean transactionExistsbyProcessId(int Id)");
		String hql = "From WtsTranTab as trans WHERE trans.processId=? ";
		int count = entityManager.createQuery(hql).setParameter(1, process).getResultList().size();
		entityManager.flush();
		return count > 0 ? true : false;
	}

	public List<WtsTransTab> gettranId(int processId) {
		System.out.println("processId" + processId);
		String hql = "FROM WtsTransTab as trans WHERE trans.processId=:proc";

		List<WtsTransTab> ls = entityManager.createQuery(hql).setParameter("proc", processId).getResultList();

		return ls;
	}

	public void addProcessTransaction(WtsTransTab transaction) {

		transaction.setEventDate(TreatmentDate.getInstance().getTreatmentDate());

		transaction.setStatusId(0);

		entityManager.persist(transaction);
		entityManager.flush();

	}

	public void updateTransactionModifiedDetail(WtsTransTab trans) throws Exception {
		WtsTransTab transa = (WtsTransTab) trans;

		Timestamp startDTTime = null;
		Timestamp endDtTime = null;
		Timestamp startModDTTime = null;
		Timestamp endModDtTime = null;
		int bufferTime = 0;
		int curSeq =0;
		// APPLICATION
		if (transa.getApplicationId() > 0) {
			WtsAppTab appln = appDAO.getAppById(transa.getApplicationId());
			WtsProcessAppMapTab proMap=proAppMapDao.getAllAppMappingsByProcess(transa.getProcessId(), transa.getApplicationId());
			if(proMap!=null) {
				 startDTTime=proMap.getStartTime();
				 endDtTime=proMap.getEndTime();
				 bufferTime=proMap.getBufferTime();
				 curSeq=proMap.getSequence();
			}
			
			//buffer Time Added
			startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);
			System.out.println("Endtimefrom database" + endDtTime);

			endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);
			WtsNewEtaTab et = etDAO.getTdyETATxnByProcessIdAppID(appln.getApplicationId(), transa.getProcessId(),
					TreatmentDate.getInstance().getTreatmentDate());
			if (et != null) {
				startDTTime = et.getNewEtaStartTransaction();
				endDtTime = et.getNewEtaEndTransaction();

				startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);

				endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);
			}
			String name=appln.getName();
			int status = getFileStatus(startModDTTime, endModDtTime, name);
			System.out.println("getFileStatus function checked and status set");

			transa.setStatusId(status);
			
			
			
			if (FileCreationTime.getStartfileCreationTime(name) != null)
				transa.setStartTransaction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(FileCreationTime.getStartfileCreationTime(name)));
			if (status == WtsTransTabController.STATUS_SUCCESS) {
				if (FileCreationTime.getEndfileCreationTime(name) != null)
					transa.setEndTransaction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(FileCreationTime.getEndfileCreationTime(name)));
				System.out.println("start file set time" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(FileCreationTime.getEndfileCreationTime(name)));
			}
			if (status == WtsTransTabController.STATUS_FAILURE) {

				List<WtsAppTab> apps = appDAO.getAllAppsByProcess(transa.getProcessId());
				if (apps != null) {
					Iterator<WtsAppTab> appssItr = apps.iterator();
					while (appssItr.hasNext()) {
						WtsAppTab app = (WtsAppTab) appssItr.next();

						if (trans.getSendemailflag() == 0) {
							emailService.sendMailRedAlert(proAppMapDao.getAppMappingEmailId(transa.getProcessId(), app.getApplicationId()));
							emailService.sendREDalertSMS(proAppMapDao.getAppMappingSupportContact(transa.getProcessId(), app.getApplicationId()));
							trans.setSendemailflag(1);
						}

					}
				}
			}
						Timestamp refstartTime=appMapDao.getAppMappingStartTime(transa.getProcessId(), transa.getApplicationId());
						Timestamp refEndTime=appMapDao.getAppMappingEndTime(transa.getProcessId(), transa.getApplicationId());
						
						transa.setAppButtonStatus(getAppButtonStatus(transa.getProcessId(), transa.getApplicationId(),transa,refstartTime,refEndTime,transa.getStartTransaction(),transa.getEndTransaction(),transa.getSendemailflag()));
						
						
			// PROCESS
		} else if (transa.getApplicationId() == 0) {
			Date startTxnTime = null;
			Date EndTxnTime = null;
			int startAppID = 0;
			int endAppID = 0;
			int status = 0;
			List<WtsAppTab> apps = appDAO.getAllAppsByProcess(transa.getProcessId());
			
			WtsProcessTab processObj=processDAO.getProcessById(transa.getProcessId());
			Timestamp processExpEndTime=processObj.getExpectedEndTime();
			if (apps != null) {
				Iterator<WtsAppTab> appssItr = apps.iterator();
				int seq = 0;
				int mSeq = proAppMapDao.getLastSeq(processObj.getProcessId());
				while (appssItr.hasNext()) {
					WtsAppTab wtsAppTab = (WtsAppTab) appssItr.next();
					int appseq=proAppMapDao.getAppMappingSequence(transa.getProcessId(), wtsAppTab.getApplicationId());
					if (appseq == 1) {
						startAppID = wtsAppTab.getApplicationId();
						status = WtsTransTabController.STATUS_IN_PROGRESS;
						WtsTransTab appTxn = this.getTransactionByAppIdProId(startAppID, processObj.getProcessId(),
								TreatmentDate.getInstance().getTreatmentDate());
						if (appTxn != null)
							transa.setStartTransaction(appTxn.getStartTransaction());
					}
					if (appseq == mSeq) {
						seq = appseq;
						endAppID = wtsAppTab.getApplicationId();
						String name = wtsAppTab.getName();
						
						WtsProcessAppMapTab proMap=proAppMapDao.getAllAppMappingsByProcess(transa.getProcessId(), endAppID);
						if(proMap!=null) {
							 startDTTime=proMap.getStartTime();
							 endDtTime=proMap.getEndTime();
							 bufferTime=proMap.getBufferTime();
							 
						}
						
						startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);

						endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);

						WtsNewEtaTab et = etDAO.getTdyETATxnByProcessIdAppID(wtsAppTab.getApplicationId(),
								transa.getProcessId(), TreatmentDate.getInstance().getTreatmentDate());
						if (et != null) {
							startDTTime = et.getNewEtaStartTransaction();
							endDtTime = et.getNewEtaEndTransaction();
							startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);

							endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);
						}
						status = getFileStatus(startModDTTime, endModDtTime, name);
						WtsTransTab appTxn = this.getTransactionByAppIdProId(endAppID, transa.getProcessId(),
								TreatmentDate.getInstance().getTreatmentDate());
						if (appTxn != null) {
							transa.setEndTransaction(appTxn.getEndTransaction());
						}
						 if(appTxn != null && appTxn.getEndTransaction()!=null ){
						
						if(DateUtility.isAfterOrSame(processExpEndTime, appTxn.getEndTransaction())) {
							etDAO.updateGreenDay(transa.getProcessId(), TreatmentDate.getInstance().getTreatmentDate());
						}
						 }

					}
					

				}
			}

			transa.setStatusId(status);
			Timestamp refstartTime=processDAO.getProcessStartTime(transa.getProcessId());
			Timestamp refEndTime=processDAO.getProcessEndTime(transa.getProcessId());
			transa.setProcessStatus(getProcessButtonStatus(transa.getProcessId(),transa,refstartTime,refEndTime,transa.getStartTransaction(),transa.getEndTransaction()));
		}

		entityManager.flush();
	}

	public Integer getAppButtonStatus(int processId, int appId, WtsTransTab transa, Timestamp refstartTime, Timestamp refEndTime, Date startTransaction,
			Date endTransaction, int sendemailstatus) {
		Timestamp current = currentTimestamp();
		int appstatus=WtsTransTabController.STATUS_YET_TO_START;	
		boolean etaExists=false;
		try {
			int bufferTm=proAppMapDao.getAppMappingBufferTime(processId, appId);
			
			WtsNewEtaTab procET = etDAO.getTdyETATxnByProcessIdAppID(appId, processId, TreatmentDate.getInstance().getTreatmentDate());
			if (procET != null) {
				etaExists = true;
			}
			
			WtsTransTab parenttxn= this.getTdyTxnByParentId(appId, processId, TreatmentDate.getInstance().getTreatmentDate());
			
		 if (parenttxn != null &&  parenttxn.getStartTransaction() != null && parenttxn.getEndTransaction() != null
					&& (parenttxn.getEndTransaction().before(DateUtility.addBufferTime(refEndTime, bufferTm)))) {
				return WtsTransTabController.STATUS_SUCCESS;
			} else if (parenttxn != null &&  parenttxn.getStartTransaction() != null && parenttxn.getEndTransaction() != null
					&& (parenttxn.getEndTransaction().after(DateUtility.addBufferTime(refEndTime, bufferTm)))) {
				return WtsTransTabController.STATUS_APP_AMBER;
			}
		 
		 else if (parenttxn != null && parenttxn.getStartTransaction() != null && parenttxn.getEndTransaction() == null
					&& !etaExists) {
				return WtsTransTabController.STATUS_SUCCESS;
			} else if (parenttxn != null && parenttxn.getStartTransaction() != null && parenttxn.getEndTransaction() == null) {
				int tempStatus=WtsTransTabController.STATUS_APP_AMBER;
				List<WtsTransTab> childTxns = getAllTdyTxnByParentId(parenttxn.getParentId(), parenttxn.getProcessId(),
						TreatmentDate.getInstance().getTreatmentDate());

				if (childTxns != null && !childTxns.isEmpty()) {
					Iterator<WtsTransTab> childItr = childTxns.iterator();
					while (childItr.hasNext()) {
						WtsTransTab dbTxn = (WtsTransTab) childItr.next();
						WtsAppMappingTab childMapping = appMapDao.getAppMappingsByParent(dbTxn.getParentId(),
								dbTxn.getChildId(), dbTxn.getProcessId());
						Timestamp origstartTime = null;
						Timestamp origendDtTime = null;
						int buffer = 0;
						if (childMapping != null) {
							origstartTime = childMapping.getStartTime();
							origendDtTime = childMapping.getEndTime();
							buffer = childMapping.getBufferTime();
							if (dbTxn !=null &&( ((dbTxn.getStatusId()==  WtsTransTabController.STATUS_IN_PROGRESS)&&(dbTxn.getEndTransaction()!=null && dbTxn.getEndTransaction().before(DateUtility.addBufferTime(origendDtTime, buffer)))) 
									|| ((dbTxn.getStatusId()==  WtsTransTabController.STATUS_IN_PROGRESS)&&(dbTxn.getStartTransaction()!=null && dbTxn.getStartTransaction().before(DateUtility.addBufferTime(origstartTime, buffer)))))){
								tempStatus= WtsTransTabController.STATUS_SUCCESS;
							 prevTempStatus=tempStatus;
							}
							
							else if(dbTxn !=null &&( ((dbTxn.getStatusId()==  WtsTransTabController.STATUS_IN_PROGRESS)&&(dbTxn.getEndTransaction()!=null && dbTxn.getEndTransaction().after(DateUtility.addBufferTime(origendDtTime, buffer)))) 
									|| ((dbTxn.getStatusId()==  WtsTransTabController.STATUS_IN_PROGRESS)&&(dbTxn.getStartTransaction()!=null && dbTxn.getStartTransaction().after(DateUtility.addBufferTime(origstartTime, buffer)))))){
								tempStatus= WtsTransTabController.STATUS_APP_AMBER;
							prevTempStatus=tempStatus;
							}
							
							else{
								tempStatus= prevTempStatus;
							}
						}

					}
				}

				return tempStatus;
			} else if (parenttxn != null && parenttxn.getStatusId()== WtsTransTabController.STATUS_FAILURE) {
				return WtsTransTabController.STATUS_APP_AMBER;
			}
		  if(parenttxn != null && parenttxn.getStartTransaction() == null && transa.getStatusId()==WtsTransTabController.STATUS_FAILURE)
			{
				return  WtsTransTabController.STATUS_APP_AMBER;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appstatus;
	}

	public boolean ApphasSendmailRedStatus(int processId, int appId, int sendemailstatus) {
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and   app.applicationId=? and app.eventDate=? and sendmailflag not in (1)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, appId).setParameter(3, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? false : true;
		}

	public Integer getAppButtonStatusForChild(int processId, int parentId,int childId, int txnstatus, Timestamp refstartTime, Timestamp refEndTime, Date startTransaction,
			Date endTransaction, int sendemailstatus) {
		Timestamp current = currentTimestamp();
		int appstatus=WtsTransTabController.STATUS_YET_TO_START;
		if(this.ApphasSendmailRedStatusForChild(processId,parentId,childId, sendemailstatus)) {
			appstatus=WtsTransTabController.STATUS_FAILURE;
		}
		else if(txnstatus==WtsTransTabController.STATUS_IN_PROGRESS) {
			appstatus=WtsTransTabController.STATUS_SUCCESS;
		}
		else if(txnstatus==WtsTransTabController.STATUS_FAILURE) {
			appstatus=WtsTransTabController.STATUS_FAILURE;
		}else if(this.isAllChildAppsGreen(processId,  parentId, childId)) {
			appstatus=WtsTransTabController.STATUS_SUCCESS;
		
		}
		
		
		return appstatus;
	}
	public boolean ApphasSendmailRedStatusForChild(int processId, int parentId, int childId, int sendemailstatus) {
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and   parentId=?  and childId = ? and app.eventDate=? and sendemailflag=1";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, parentId).setParameter(3, childId).setParameter(4, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? true : false;
		}

	public boolean isAllChildAppsGreen(int processId, int parentId, int childId) {
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and   parentId=?  and childId = ? and app.eventDate=? and statusId in (1)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, parentId).setParameter(3, childId).setParameter(4, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? true : false;
		}
	
	public boolean isAllProcessGreen(int processId, int parentId) {
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.parentId=? and app.eventDate=? and app.appButtonStatus not in (1)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, parentId).setParameter(3, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? false : true;
	}

	private boolean isAnyProcessAppsRed(int processId, int parentId) {
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.parentId = ? and app.eventDate=? and app.appButtonStatus  in (2)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, processId).setParameter(3, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? true : false;
	}

	public boolean isAllProcessAppsGreen(int processId, int parentId) {
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.parentId=? and app.eventDate=? and app.appButtonStatus not in (0,2,5)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, parentId).setParameter(3, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? false : true;
	}
	
	public boolean isAllProcessAppsNotStarted(int processId, int parentId) {
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.parentId = ? and app.eventDate=? and app.appButtonStatus not in (0)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, parentId).setParameter(3, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? false : true;
		}
	
	public Integer getProcessButtonStatus(int processId, WtsTransTab transa, Timestamp refstartTime, Timestamp refEndTime, Date startTransaction,
			Date endTransaction) {
			boolean etaExists=false;
		WtsNewEtaTab procET = etDAO.getTdyETATxnByProcessId(processId,  TreatmentDate.getInstance().getTreatmentDate());
		if (procET != null) {
			etaExists=true;
		}
		    if (this.isAllProcessAppsNotStarted(processId)){
		    	return WtsTransTabController.STATUS_YET_TO_START;
		    }
	
		    else if(transa!=null && transa.getEndTransaction()!=null && (transa.getEndTransaction().after(refEndTime))){
				
				return WtsTransTabController.STATUS_FAILURE;
			}else if(transa!=null && transa.getEndTransaction()!=null && (transa.getEndTransaction().before(refEndTime))){
				return WtsTransTabController.STATUS_SUCCESS;
			}
			else if(transa!=null && transa.getEndTransaction()==null){
					if(this.isAnyParentAppsAmber(transa.getProcessId()))
						return WtsTransTabController.STATUS_PROC_ORANGE;
					else
						return WtsTransTabController.STATUS_PROC_LIGHTGREEN;
			}
			else if(transa!=null && transa.getStartTransaction()!=null && transa.getEndTransaction()==null && !etaExists){
				return WtsTransTabController.STATUS_PROC_LIGHTGREEN;
			}
			
		
		
		return WtsTransTabController.STATUS_YET_TO_START;
	}
	
	private Date processEndTime(int processId) {
		String hql="select endTransaction from WtsTransTab as tran where tran.processId=? and eventDate=? and parentId is NULL and childId is NULL and applicationId is NULL";
		Date endTime=(Date) entityManager.createQuery(hql).setParameter(1,processId).setParameter(2,TreatmentDate.getInstance().getTreatmentDate()).getSingleResult();
	   return endTime;
	}

	public boolean isAllChildAppsGreen(int processId,int applicationId){
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and (applicationId=? or  parentId=? ) and app.eventDate=? and statusId not in (1)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, applicationId).setParameter(3, applicationId).setParameter(4, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? false : true;
		}
	
	public boolean isAnyParentAppsAmber(int processId){
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.eventDate=? and appButtonStatus = 5 and childId is NULL and applicationId is not null";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? true : false;
		}
	
	public boolean isAllProcessGreen(int processId){
		String hql= "FROM WtsTransTab as app WHERE app.processId = ?  and app.eventDate=? and statusId not in (1)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? false : true;
		}
	public boolean isAllProcessAppsNotStarted(int processId){
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.eventDate=? and app.appButtonStatus not in (0)";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? false : true;
		}
	public boolean isAllProcessAppsGreen(int processId){
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.eventDate=? and app.appButtonStatus not in (0,2,5) and childId>0";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? true : false;
		}
	
	
	public boolean isAnyProcessAppsRed(int processId){
		String hql= "FROM WtsTransTab as app WHERE app.processId = ? and app.eventDate=? and app.appButtonStatus  in (2) ";
		int cnt = entityManager.createQuery(hql).setParameter(1, processId).setParameter(2, TreatmentDate.getInstance().getTreatmentDate()).getResultList().size();
		return cnt > 0 ? true : false;
		}
	
	
	public void updateChildTransactionModifiedDetail(WtsTransTab trans, boolean mainpageNav) throws Exception {
		WtsTransTab transa = (WtsTransTab) trans;

		Timestamp startDTTime = null;
		Timestamp endDtTime = null;
		Timestamp startModDTTime = null;
		Timestamp endModDtTime = null;
		int bufferTime = 0;
		int curSeq =0;
		// child
		if (transa.getChildId() > 0 && transa.getApplicationId()==0) {
			WtsAppTab appln = appDAO.getAppById(transa.getChildId());
			WtsAppMappingTab proMap=appMapDao.getAppMappingsByParent(transa.getParentId(), transa.getChildId(),transa.getProcessId());
			if(proMap!=null) {
				 startDTTime=proMap.getStartTime();
				 endDtTime=proMap.getEndTime();
				 bufferTime=proMap.getBufferTime();
				 curSeq=proMap.getSequence();
			}
			
			//buffer time added
			startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);
			System.out.println("Endtimefrom database" + endDtTime);
			boolean etaExists=false;
			endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);
			WtsNewEtaTab et = etDAO.getTdyETATxnByParentChildID(transa.getParentId(), transa.getChildId(),transa.getProcessId(),
					TreatmentDate.getInstance().getTreatmentDate());
			if (et != null) {
				startDTTime = et.getNewEtaStartTransaction();
				endDtTime = et.getNewEtaEndTransaction();

				startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);

				endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);
				etaExists=true;
			}
			String name=appln.getName();
			int status = getFileStatus(startModDTTime, endModDtTime, name);
			boolean etaCalculated=false;
			WtsTransTab existtrans = getTransactionByParentChildId( transa.getChildId(),transa.getParentId(),transa.getProcessId(),
					TreatmentDate.getInstance().getTreatmentDate());
			
			
			
			if (existtrans != null && existtrans.getStatusId() == WtsTransTabController.STATUS_FAILURE
					&& (status == WtsTransTabController.STATUS_IN_PROGRESS
							|| status == WtsTransTabController.STATUS_DELAYED
							|| status == WtsTransTabController.STATUS_FAILURE)) {

				// UPDATE ETA HERE FOR ALL NEXT APPS AND PROCESS
				this.updateNewChildETA(transa.getProcessId(), existtrans.getParentId(), existtrans.getChildId(), true,mainpageNav);
				etaCalculated=true;
				existtrans.setAppButtonStatus(WtsTransTabController.STATUS_FAILURE);

			} else if (existtrans != null && existtrans.getStatusId() == WtsTransTabController.STATUS_FAILURE
					&& (status == WtsTransTabController.STATUS_SUCCESS)) {
				if (FileCreationTime.getStartfileCreationTime(name) != null
						&& (FileCreationTime.getEndfileCreationTime(name) != null)
						&& (FileCreationTime.endTimestamp(name).after(endModDtTime)))
					// UPDATE ETA HERE FOR ALL NEXT APPS AND PROCESS
					this.updateNewChildETA(transa.getProcessId(), existtrans.getParentId(), existtrans.getChildId(), true,mainpageNav);
				etaCalculated=true;
			}else if (existtrans != null && existtrans.getStatusId() == WtsTransTabController.STATUS_IN_PROGRESS
					&& (status == WtsTransTabController.STATUS_SUCCESS)) {
				if (FileCreationTime.getStartfileCreationTime(name) != null
						&& (FileCreationTime.getEndfileCreationTime(name) != null)
						&& (FileCreationTime.endTimestamp(name).before(endModDtTime))
						&& etaExists) {
					// UPDATE ETA HERE FOR ALL NEXT APPS AND PROCESS, if ETA finishes early
					this.updateNewChildETA(transa.getProcessId(), existtrans.getParentId(), existtrans.getChildId(), true,mainpageNav);
				etaCalculated=true;
				}
			}

			System.out.println("getFileStatus function checked and status set");

			transa.setStatusId(status);
			
			
			if (FileCreationTime.getStartfileCreationTime(name) != null)
				transa.setStartTransaction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(FileCreationTime.getStartfileCreationTime(name)));
			if (status == WtsTransTabController.STATUS_SUCCESS) {
				if (FileCreationTime.getEndfileCreationTime(name) != null)
					transa.setEndTransaction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(FileCreationTime.getEndfileCreationTime(name)));
				System.out.println("start file set time" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(FileCreationTime.getEndfileCreationTime(name)));
			}
			if (status == WtsTransTabController.STATUS_FAILURE) {

				List<WtsAppMappingTab> apps = appMapDao.getAllAppMappingsByParent(transa.getParentId(),transa.getProcessId());
				if (apps != null) {
					Iterator<WtsAppMappingTab> appssItr = apps.iterator();
					while (appssItr.hasNext()) {
						WtsAppMappingTab app = (WtsAppMappingTab) appssItr.next();

						if (trans.getSendemailflag() == 0) {
							emailService.sendMailRedAlert(app.getEmailId());
							emailService.sendREDalertSMS(app.getSupportContact());
							trans.setSendemailflag(1);
						}

					}
				}
			}

			Timestamp refstartTime=appMapDao.getAppMappingStartTime(transa.getProcessId(), transa.getParentId(),transa.getChildId());
			Timestamp refEndTime=appMapDao.getAppMappingEndTime(transa.getProcessId(), transa.getParentId(),transa.getChildId());
			
			transa.setAppButtonStatus(getAppButtonStatusForChild(transa.getProcessId(), transa.getParentId(),transa.getChildId(),transa.getStatusId(),refstartTime,refEndTime,transa.getStartTransaction(),transa.getEndTransaction(),transa.getSendemailflag()));
			// Parent
		} else if (transa.getChildId() == 0  && transa.getApplicationId()==0 && transa.getParentId()!=0) {
			Date startTxnTime = null;
			Date EndTxnTime = null;
			int startAppID = 0;
			int currentAppID = 0;
			int status = 0;
			List<WtsAppTab> apps = appDAO.getAllAppsByProcess(transa.getParentId());
			List<WtsAppMappingTab> appMappings=appMapDao.getAllAppMappingsByParent(transa.getParentId() ,transa.getProcessId());
			WtsProcessTab processObj=processDAO.getProcessById(transa.getProcessId());
			Timestamp processExpEndTime=processObj.getExpectedEndTime();
			if (appMappings != null) {
				Iterator<WtsAppMappingTab> appssItr = appMappings.iterator();
				int seq = 0;
				int childId=0;
				int mSeq = appMapDao.getLastSeq(processObj.getProcessId(),transa.getParentId());
				
				while (appssItr.hasNext()) {
					WtsAppMappingTab wtsAppTab = (WtsAppMappingTab) appssItr.next();
					
					int appseq=wtsAppTab.getSequence();
					if (appseq == 1) {
						startAppID = wtsAppTab.getChildId();
						status = WtsTransTabController.STATUS_IN_PROGRESS;
						WtsTransTab appTxn = this.getTransactionByParentChildId(startAppID,transa.getParentId(),transa.getProcessId(),
								TreatmentDate.getInstance().getTreatmentDate());
						if (appTxn != null)
							transa.setStartTransaction(appTxn.getStartTransaction());
					}
					if (appseq <= mSeq) {
						seq = appseq;
						currentAppID = wtsAppTab.getChildId();
						String name = appDAO.getAppById(currentAppID).getName();
						
						WtsAppMappingTab proMap=appMapDao.getAppMappingsByParent(wtsAppTab.getParentId(),currentAppID, transa.getProcessId());
						if(proMap!=null) {
							 startDTTime=proMap.getStartTime();
							 endDtTime=proMap.getEndTime();
							 bufferTime=proMap.getBufferTime();
							 
						}
						
						startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);

						endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);

						WtsNewEtaTab et = etDAO.getTdyETATxnByParentChildID(transa.getParentId(), transa.getChildId(),transa.getProcessId(),
								TreatmentDate.getInstance().getTreatmentDate());
						if (et != null) {
							startDTTime = et.getNewEtaStartTransaction();
							endDtTime = et.getNewEtaEndTransaction();
							startModDTTime = DateUtility.addBufferTime(startDTTime, bufferTime);

							endModDtTime = DateUtility.addBufferTime(endDtTime, bufferTime);
						}
						status = getFileStatus(startModDTTime, endModDtTime, name);
						WtsTransTab appParent= this.getTdyTxnByProcessIdAppId(transa.getProcessId(), TreatmentDate.getInstance().getTreatmentDate(), transa.getParentId());
						if(appParent!=null)
						status=appParent.getStatusId();
						WtsTransTab appTxn = this.getTransactionByParentChildId(currentAppID,transa.getParentId(),transa.getProcessId(),
								TreatmentDate.getInstance().getTreatmentDate());
						if (appTxn != null && appseq==mSeq ) {
							transa.setEndTransaction(appTxn.getEndTransaction());
							
						}
						 if(appTxn != null && appTxn.getEndTransaction()!=null ){
							 status=1;
						if(DateUtility.isAfterOrSame(processExpEndTime, appTxn.getEndTransaction())) {
							etDAO.updateGreenDayForChilds(transa.getProcessId(),transa.getParentId(), TreatmentDate.getInstance().getTreatmentDate());
						}
						 }

					}
					

				}
			}

			transa.setStatusId(status);
			Timestamp refstartTime=appMapDao.getAppMappingStartTime(transa.getProcessId(), transa.getParentId());
			Timestamp refEndTime=appMapDao.getAppMappingEndTime(transa.getProcessId(), transa.getParentId());
			
			
		}

		entityManager.flush();
	}
	
	private void updateNewChildETA(int processId, int parentId, int childId, boolean isProblem, boolean mainpageNav) throws ParseException {
		System.out.println("updateupdateNewChildETA entered");
		// MOVE THIS to the ETA SERVICE

		etDAO.newEtaCalculationForChilds(parentId,childId, processId,mainpageNav);
	}

	private int getFileStatus(Timestamp startDTTime, Timestamp endDtTime, String name) {
		int finalstatus = 0;
		try {
			System.out.println("getFileStatus Loop entered");

			boolean startFile = FileCreationTime.stratFileExist(name);
			boolean endFile = FileCreationTime.endFileExist(name);
			boolean failFile = FileCreationTime.failFileExist(name);

			Timestamp current = currentTimestamp();
			if ((!startFile) && (!endFile) && (!failFile) && current.after(startDTTime)) {
				finalstatus = 2;
				System.out.println("Delayed RED.");
			} else if ((!startFile) && (!endFile) && (!failFile)) {
				finalstatus = 0;
				System.out.println("not started");
			}

			else if ((!startFile) && (!endFile) && current.after(startDTTime)) {

				finalstatus = 2;
			}

			else if ((startFile) && (!endFile) && (failFile)) {

				finalstatus = 2;
			}

			else if ((startFile) && (!endFile) && (current.after(endDtTime))) {
				finalstatus = 2;
				System.out.println("application completion delayed");
			} else if ((startFile) && (!endFile) && current.before(endDtTime)) {
				finalstatus = 4;
				System.out.println("app still running");
			}

			System.out.println("current time is " + current);
			System.out.println("expectde start time is---- " + startDTTime);
			System.out.println("start file present and conditions checked");

			if ((startFile) && (endFile)) {
				System.out.println("application completed successfully!!!!");
				finalstatus = 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("loop exit with status as " + finalstatus);
		return finalstatus;
	}

	public List<WtsAppTab> getAppById(int applicationId) {
		String hql = "FROM WtsAppTab as trans inner join WtsTransTab  as tr where tr.application=?  ";
		return (List<WtsAppTab>) entityManager.createQuery(hql).setParameter(1, applicationId).getResultList();
	}

	public List<WtsTransTab> getFlowData(int applicationId) {
		return null;
	}

	public Timestamp startTimestamp(String name) {
		String startActTime = FileCreationTime.getStartfileCreationTime(name);
		Timestamp start = Timestamp.valueOf(startActTime);
		return start;
	}

	public Timestamp endTimestamp(String name) {
		String endActTime = FileCreationTime.getEndfileCreationTime(name);
		Timestamp end = Timestamp.valueOf(endActTime);
		return end;
	}

	public Timestamp currentTimestamp() {
		Date today = new Date();
		Timestamp current = new Timestamp(today.getTime());
		return current;

	}

	public void EtaMail(int processId) {
		List<WtsNewEtaTab> eta = etDAO.getAllCurrentEta();
		if (eta != null) {
			Iterator<WtsNewEtaTab> etaItr = eta.iterator();
			while (etaItr.hasNext()) {
				WtsNewEtaTab et = etaItr.next();
				if (et.getApplicationId() > 0) {
					WtsAppTab app = appDAO.getAppById(et.getApplicationId());
					if (app != null) {
						WtsTransTab tr = getTdyTxnByProcessIdAppId(processId,
								TreatmentDate.getInstance().getTreatmentDate(), app.getApplicationId());
						if (tr != null) {
							if (tr.getSendetaemailflag() == 0) {
								emailService.SendMailAlertNewEta(proAppMapDao.getAppMappingEmailId(processId, app.getApplicationId()));
								tr.setSendetaemailflag(1);
							}
						}
					}

				}
			}

		}
		System.out.println("new eta mail sent........");
	}
}
