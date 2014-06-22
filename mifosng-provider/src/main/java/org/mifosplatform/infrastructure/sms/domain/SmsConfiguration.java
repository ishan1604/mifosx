package org.mifosplatform.infrastructure.sms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "sms_configuration")
public class SmsConfiguration extends AbstractPersistable<Long> {
	@Column(name = "name", nullable = false)
    private String name;
	
	@Column(name = "value", nullable = false)
    private String value;
	
	/** 
	 * SmsConfiguration constructor 
	 **/
	protected SmsConfiguration() {}
	
	/** 
	 * SmsConfiguration constructor 
	 **/
	public SmsConfiguration(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
