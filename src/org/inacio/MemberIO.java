package org.inacio;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.inacio.Member;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class MemberIO {
	final static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	final static Logger LOG = Logger.getLogger(MemberIO.class.getName());

	public static Member get(String p) {
		if(p == null) { return null; }
		else { return get(KeyFactory.stringToKey(p)); }		
	}
	
	public static Member get(Key k) {
		Entity e;
		Member m;
		if(k == null) {
			return null;
		}
		try {
			e = datastore.get(k);
			m = setMember(e);
			return m;
		} catch (EntityNotFoundException e1) {
			LOG.log(Level.SEVERE,e1.getMessage());
			e = null;
			return null;
		}
	}
	
	public static Member setMember(Entity e)
	{
		Member m = null;
		if(e != null) {
			m = new Member();
			m.setAddress((String) e.getProperty("address"));
			m.setCsz((String) e.getProperty("csz"));
			m.setDatePaid((Long) e.getProperty("datepaid"));
			m.setEmail((String) e.getProperty("email"));
			m.setId(e.getKey());
			m.setName((String) e.getProperty("name"));
			m.setPhone((String) e.getProperty("phone"));
			m.setRate((String) e.getProperty("rate"));
			m.setTotalAmt((double) e.getProperty("totalamt"));
			m.setTransactionId((String) e.getProperty("transactionid"));
		}
		return m;		
	}

	public static Key add(Member m)
	{
		Entity e = null;
		if(m.getId() != null) {
			try {
				e = datastore.get(m.getId());
			} catch (EntityNotFoundException e1) {
				LOG.log(Level.SEVERE, e1.getMessage());
				e = null;
			}
		}
		if( e == null) { // Handle as new add
			e = new Entity(Member.class.getName());
		}
		e.setProperty("address", m.getAddress());
		e.setProperty("csz", m.getCsz());
		e.setProperty("datepaid", m.getDatePaid());
		e.setProperty("email", m.getEmail());
		e.setProperty("name", m.getName());
		e.setProperty("phone", m.getPhone());
		e.setProperty("rate", m.getRate());
		e.setProperty("totalamt", m.getTotalAmt());
		e.setProperty("transactionid", m.getTransactionId());
		Key k = datastore.put(e);
		m.setId(k);
		return k;
	}
}
