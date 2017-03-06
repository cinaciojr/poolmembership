package org.inacio;

import java.io.IOException;

public class SimplePay {

	/*
	 * Required applicationName
	 */
	protected String applicationName;
	/*
	 * Required APICredential
	 */
	protected APICredential credentialObj;
	/*
	 * Required Receiver info
	 */
	protected Receiver receiver;
	/*
	 * Required Environment
	 */
	protected ServiceEnvironment env;
    /*
     * Required memo
     */
    protected String memo;
    /*
     * Required CurrencyCode
     */
    protected CurrencyCodes currencyCode;
    /*
     * Required language for localization
     */
    protected String language;
    /*
     * Required CancelUrl
     */
    protected String cancelUrl;
    /*
     * Required returnUrl
     */
    protected String returnUrl;
    /*
     * Required User IP Address
     */
    protected String userIp;
    /*
     * Optional ipnURL
     */
    protected String ipnURL;
    /*
	 * Optional Sender Email
	 */
	protected String senderEmail;
    
    // internal field
    protected boolean requestProcessed = false;
    
    /*
     * Default constructor
     */
    public SimplePay(){
		
	}
    
	public PayResponse makeRequest() throws IOException, Exception {
		
		validate();
		
		PaymentDetails paymentDetails = new PaymentDetails(ActionType.PAY);
		PayRequest payRequest = new PayRequest(language, env);
		paymentDetails.addToReceiverList(receiver);
		if(ipnURL != null){
			paymentDetails.setIpnNotificationUrl(ipnURL);
		}
		paymentDetails.setCurrencyCode(currencyCode);
		paymentDetails.setCancelUrl(cancelUrl);
		paymentDetails.setReturnUrl(returnUrl);
		if(senderEmail != null && senderEmail.length() > 0){
			paymentDetails.setSenderEmail(senderEmail);
		}
		// set clientDetails
		ClientDetails clientDetails = new ClientDetails();
		clientDetails.setIpAddress(userIp);
		clientDetails.setApplicationId(applicationName);
		payRequest.setClientDetails(clientDetails);
		
		// set payment details
		payRequest.setPaymentDetails(paymentDetails);
		PayResponse payResp = payRequest.execute(credentialObj);
		 // if there is an API level error handle those first - look for responseEnvelope/ack
        if(payResp.getResponseEnvelope().getAck() == AckCode.Failure || payResp.getResponseEnvelope().getAck() == AckCode.FailureWithWarning){
                // throw error
        	String errMsg = payResp.getResponseEnvelope().toString();
        	for(int i=0;i<payResp.getPayErrorList().size();i++) {
        		errMsg += payResp.getPayErrorList().get(i);
        	}
            throw new Exception(errMsg);
        }

        // if it's a payment execution error throw an exception
        if(payResp.getPaymentExecStatus() != null){
                if(payResp.getPaymentExecStatus() == PaymentExecStatus.ERROR ) {
                	String errMsg = payResp.getPaymentExecStatus().toString();
                        //PaymentExecException peex = new PaymentExecException(payResp.getPaymentExecStatus());
                	for(int i=0;i<payResp.getPayErrorList().size();i++) {
                		errMsg += payResp.getPayErrorList().get(i);
                	}
                        //peex.setPayErrorList(payResp.getPayErrorList());
                	errMsg += payResp.getResponseEnvelope();
                        //peex.setResponseEnvelope(payResp.getResponseEnvelope());
                    throw new Exception(errMsg);

                } else if( payResp.getPaymentExecStatus() == PaymentExecStatus.INCOMPLETE || payResp.getPaymentExecStatus() == PaymentExecStatus.REVERSALERROR ){
                        //PaymentInCompleteException ex = new PaymentInCompleteException(payResp.getPaymentExecStatus());
                	String errMsg = payResp.getPaymentExecStatus().toString();
                	for(int i=0;i<payResp.getPayErrorList().size();i++) {
                		errMsg += payResp.getPayErrorList().get(i);
                	}
                        //ex.setPayErrorList(payResp.getPayErrorList());
                	errMsg += payResp.getPayKey();
                        //ex.setPayKey(payResp.getPayKey());
                	errMsg += payResp.getResponseEnvelope();
                        //ex.setResponseEnvelope(payResp.getResponseEnvelope());
                	throw new Exception(errMsg);
                        //throw ex;

                } else if(payResp.getPaymentExecStatus() == PaymentExecStatus.CREATED){
                        // throw exception to redirect user for authorization
                        //AuthorizationRequiredException ex = new AuthorizationRequiredException();
                        //ex.setPayKey(payResp.getPayKey());
                        //throw ex;
                	//  Nothing to be done - handled down the line.
                } else if(payResp.getPaymentExecStatus() == PaymentExecStatus.COMPLETED
                                || payResp.getPaymentExecStatus() == PaymentExecStatus.PROCESSING
                                || payResp.getPaymentExecStatus() == PaymentExecStatus.PENDING){
                        // no further action required so treat these as success
                } else {
                        // unknown paymentExecStatus - throw exception
                	String errMsg = payResp.getPaymentExecStatus().toString();
                        //PaymentExecException peex = new PaymentExecException(payResp.getPaymentExecStatus());
                	for(int i=0;i<payResp.getPayErrorList().size();i++) {
                		errMsg += payResp.getPayErrorList().get(i);
                	}
                        //peex.setPayErrorList(payResp.getPayErrorList());
                	errMsg += payResp.getResponseEnvelope();
                        //peex.setResponseEnvelope(payResp.getResponseEnvelope());
                	throw new Exception(errMsg);
                        //throw peex;
                }
        }
		return payResp;
	}
	
	public void validate() throws Exception {
		
		if(requestProcessed){
			// throw error
			throw new Exception("RequestAlreadyMadeException");
		}
		if(language == null){
			// throw error
			throw new Exception("MissingParameterException:language");
		}
		if(receiver == null){
			// throw error
			throw new Exception("MissingParameterException:Receiver");
		}
		if(currencyCode == null){
			// throw error
			throw new Exception("MissingParameterException:CurrencyCode");
		}
		if(env == null){
			// throw error
			throw new Exception("MissingParameterException:ServiceEnvironment");
		}
		if(memo == null){
			// throw error
			throw new Exception("MissingParameterException:memo");
		}
		if(returnUrl == null){
			// throw error
			throw new Exception("MissingParameterException:returnUrl");
		}
		if(cancelUrl == null){
			// throw error
			throw new Exception("MissingParameterException:cancelUrl");
		}
		if(userIp == null){
			// throw error
			throw new Exception("MissingParameterException:userIp");
		}
		if(applicationName == null){
			// throw applicationName
			throw new Exception("MissingParameterException:applicationName");
		}
	}

	/**
	 * @return the credentialObj
	 */
	public APICredential getCredentialObj() {
		return credentialObj;
	}

	/**
	 * @param credentialObj the credentialObj to set
	 */
	public void setCredentialObj(APICredential credentialObj) {
		this.credentialObj = credentialObj;
	}

	/**
	 * @return the receiver
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the env
	 */
	public ServiceEnvironment getEnv() {
		return env;
	}

	/**
	 * @param env the env to set
	 */
	public void setEnv(ServiceEnvironment env) {
		this.env = env;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return the currencyCode
	 */
	public CurrencyCodes getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(CurrencyCodes currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the cancelUrl
	 */
	public String getCancelUrl() {
		return cancelUrl;
	}

	/**
	 * @param cancelUrl the cancelUrl to set
	 */
	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	/**
	 * @return the returnUrl
	 */
	public String getReturnUrl() {
		return returnUrl;
	}

	/**
	 * @param returnUrl the returnUrl to set
	 */
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	/**
	 * @return the ipnURL
	 */
	public String getIpnURL() {
		return ipnURL;
	}

	/**
	 * @param ipnURL the ipnURL to set
	 */
	public void setIpnURL(String ipnURL) {
		this.ipnURL = ipnURL;
	}

	/**
	 * @return the senderEmail
	 */
	public String getSenderEmail() {
		return senderEmail;
	}

	/**
	 * @param senderEmail the senderEmail to set
	 */
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	/**
	 * @return the userIp
	 */
	public String getUserIp() {
		return userIp;
	}

	/**
	 * @param userIp the userIp to set
	 */
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
}
