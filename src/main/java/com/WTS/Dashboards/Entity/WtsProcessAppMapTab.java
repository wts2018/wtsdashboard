package com.WTS.Dashboards.Entity;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
@Table(name="wts_process_app_map_tab")
@javax.persistence.Cacheable
public class WtsProcessAppMapTab implements Serializable { 
                private static final long serialVersionUID = 1L;
                @Id
                //@GeneratedValue(strategy=GenerationType.AUTO)
                @Column(name="process_app_map_id")
                private int processAppMapId;  
                @Column(name="name")
                private String name;
                @Column(name="process_id")                
                private int processId;
                @Column(name="application_id")                
                private int applicationId;
                @Column(name="sequence")
                private int sequence;
                @Column(name="trig_id")
                private int trigId;
                @Column(name="comments")
                private String comments;
                @Column(name="weight")
                private int weight;
                @Column(name="last_update_time")
                @JsonFormat(timezone= "IST", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                private Timestamp lastUpdateTime;
                @Column(name="enable_flag")
                
                private int enableFlag;
                @Column(name="buffer_minute_time")
                private int bufferTime;
                @Column(name="email_id")
                private String emailId;
               
                @Column(name="support_contact")
                private String supportContact;
                
                
                @Column(name="start_time")
                @JsonFormat(timezone= "IST", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                private Timestamp startTime;
                
                @Column(name="end_time")
                @JsonFormat(timezone= "IST", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                private Timestamp endTime;
                
                @ManyToOne(fetch=FetchType.EAGER)
                @JoinColumn(name = "process_id", nullable = false,insertable=false,updatable=false) 
                private WtsProcessTab processApp;
                @ManyToOne(fetch=FetchType.EAGER)
                @JoinColumn(name = "application_id", nullable = false,insertable=false,updatable=false) 
    private WtsAppTab application;
                
                @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentId")
                @JsonProperty("Transactions")
    private Set<WtsTransTab> tran = new HashSet<WtsTransTab>();
                
                @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentId")
                @JsonProperty("NewETA")
    private Set<WtsNewEtaTab> eta = new HashSet<WtsNewEtaTab>();
                
               
                public String getName() {
                                return name;
                }
                public void setName(String name) {
                                this.name = name;
                }
               
                public int getSequence() {
                                return sequence;
                }
                public void setSequence(int sequence) {
                                this.sequence = sequence;
                }
                public int getTrigId() {
                                return trigId;
                }
                public void setTrigId(int trigId) {
                                this.trigId = trigId;
                }
                public String getComments() {
                                return comments;
                }
                public void setComments(String comments) {
                                this.comments = comments;
                }
                public int getWeight() {
                                return weight;
                }
                public void setWeight(int weight) {
                                this.weight = weight;
                }
                public Timestamp getLastUpdateTime() {
                                return lastUpdateTime;
                }
                public void setLastUpdateTime(Timestamp lastUpdateTime) {
                                this.lastUpdateTime = lastUpdateTime;
                }
                public int getEnableFlag() {
                                return enableFlag;
                }
                public void setEnableFlag(int enableFlag) {
                                this.enableFlag = enableFlag;
                }
                public int getBufferTime() {
                                return bufferTime;
                }
                public void setBufferTime(int bufferTime) {
                                this.bufferTime = bufferTime;
                }
               
                public Timestamp getStartTime() {
                                return startTime;
                }
                public void setStartTime(Timestamp startTime) {
                                this.startTime = startTime;
                }
                public Timestamp getEndTime() {
                                return endTime;
                }
                public void setEndTime(Timestamp endTime) {
                                this.endTime = endTime;
                }

                public String getEmailId() {
                                return emailId;
                }
                public void setEmailId(String emailId) {
                                this.emailId = emailId;
                }
				public String getSupportContact() {
					return supportContact;
				}
				public void setSupportContact(String supportContact) {
					this.supportContact = supportContact;
				}
				
				
				public int getProcessAppMapId() {
					return processAppMapId;
				}
				public void setProcessAppMapId(int processAppMapId) {
					this.processAppMapId = processAppMapId;
				}
				public int getProcessId() {
					return processId;
				}
				public void setProcessId(int processId) {
					this.processId = processId;
				}
				public int getApplicationId() {
					return applicationId;
				}
				public void setApplicationId(int applicationId) {
					this.applicationId = applicationId;
				}
				public WtsProcessTab getProcessApp() {
					return processApp;
				}
				public void setProcessApp(WtsProcessTab processApp) {
					this.processApp = processApp;
				}
				public WtsAppTab getApplication() {
					return application;
				}
				public void setApplication(WtsAppTab application) {
					this.application = application;
				}
				public Set<WtsTransTab> getTran() {
					return tran;
				}
				public void setTran(Set<WtsTransTab> tran) {
					this.tran = tran;
				}
				public Set<WtsNewEtaTab> getEta() {
					return eta;
				}
				public void setEta(Set<WtsNewEtaTab> eta) {
					this.eta = eta;
				}
                
				
                
}
