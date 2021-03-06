package org.inacio;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PayRequest extends PayPalBaseRequest {

	private static final Logger log = Logger.getLogger(PayRequest.class.getName());

	protected ClientDetails clientDetails;
	protected PaymentDetails paymentDetails;

	public PayRequest(String language, ServiceEnvironment env) {

		requestEnvelope = new RequestEnvelope();
		requestEnvelope.setErrorLanguage(language);
		this.env = env;
		paymentDetails = new PaymentDetails(ActionType.PAY);

	}

	public PayResponse execute(APICredential credentialObj) throws Exception {

		String responseString = "";

		/* - VALIDATE REQUIRED PARAMS- */
		/*
		 * check for the following things 1. API Credentials 2. Atleast one
		 * receiver has been set 3. CurrencyCode is set
		 */
		if (credentialObj == null) {
			throw new Exception("MissingAPICredentialsException");
		} else if (credentialObj != null) {
			String ex = "";
			if (credentialObj.getAppId() == null || credentialObj.getAppId().length() <= 0) {
				ex += " AppId";
			}
			if (credentialObj.getAPIPassword() == null || credentialObj.getAPIPassword().length() <= 0) {
				ex +=" APIPassword";
			}

			if (credentialObj.getAPIUsername() == null || credentialObj.getAPIUsername().length() <= 0) {
				ex +=" APIUsername";
			}
			if (credentialObj.getSignature() == null || credentialObj.getSignature().length() <= 0) {
				ex += " Signature";
			}
			if (!ex.equals("")) {
				throw new Exception("InvalidCredentialException:"+ex);
			} else {
				ex = null;
			}
		}

		if (this.paymentDetails.getReceiverList().size() <= 0) {
			throw new Exception("MissingParameterException: Receiver");
		}

		if (this.paymentDetails.getCurrencyCode() == null) {
			throw new Exception("MissingParameterException:CurrencyCode");
		}

		/* -END-VALIDATION- */

		// prepare request parameters
		StringBuilder postParameters = new StringBuilder();

		// add request envelope
		postParameters.append(requestEnvelope.serialize());
		postParameters.append(ParameterUtils.PARAM_SEP);

		// add payment details
		postParameters.append(this.paymentDetails.serialize());

		// set clientDetails
		postParameters.append(this.clientDetails.serialize());

		if (log.isLoggable(Level.INFO))
			log.info("Sending PayRequest with: " + postParameters.toString());

		// send request
		responseString = makeRequest(credentialObj, "Pay", postParameters.toString());

		// parse response
		PayResponse response = new PayResponse(responseString);

		// handle errors
		return response;
	}

	/**
	 * @param clientDetails
	 *            the clientDetails to set
	 */
	public void setClientDetails(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}

	/**
	 * @return the clientDetails
	 */
	public ClientDetails getClientDetails() {
		return clientDetails;
	}

	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public String toString() {

		StringBuilder outStr = new StringBuilder();

		outStr.append("<table border=1>");
		outStr.append("<tr><th>");
		outStr.append(this.getClass().getSimpleName());
		outStr.append("</th><td></td></tr>");
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(this.getClass(), Object.class);
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				try {
					String name = pd.getName();
					Object value = this.getClass().getDeclaredField(name)
							.get(this);
					if (value != null) {
						outStr.append("<tr><td>");
						outStr.append(pd.getName());
						outStr.append("</td><td>");
						outStr.append(value.toString());
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outStr.append("</td></tr>");
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outStr.append("</table>");
		return outStr.toString();

	}

}
