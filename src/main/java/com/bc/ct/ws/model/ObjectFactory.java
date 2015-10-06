//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vIBM 2.2.3-07/07/2014 12:54 PM(foreman)- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.06 at 02:04:57 PM MDT 
//


package com.bc.ct.ws.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.boiseinc.cc.webservice.rating package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.boiseinc.cc.webservice.rating
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RateRequest }
     * 
     */
    public RateRequest createRateRequest() {
        return new RateRequest();
    }

    /**
     * Create an instance of {@link RateRequest.Stops }
     * 
     */
    public RateRequest.Stops createRateRequestStops() {
        return new RateRequest.Stops();
    }

    /**
     * Create an instance of {@link RateRequest.Commoditys }
     * 
     */
    public RateRequest.Commoditys createRateRequestCommoditys() {
        return new RateRequest.Commoditys();
    }

    /**
     * Create an instance of {@link RateQuote }
     * 
     */
    public RateQuote createRateQuote() {
        return new RateQuote();
    }

    /**
     * Create an instance of {@link RateQuote.Charges }
     * 
     */
    public RateQuote.Charges createRateQuoteCharges() {
        return new RateQuote.Charges();
    }

    /**
     * Create an instance of {@link RateResponse }
     * 
     */
    public RateResponse createRateResponse() {
        return new RateResponse();
    }

    /**
     * Create an instance of {@link RateFailure }
     * 
     */
    public RateFailure createRateFailure() {
        return new RateFailure();
    }

    /**
     * Create an instance of {@link RateRequest.Equipment }
     * 
     */
    public RateRequest.Equipment createRateRequestEquipment() {
        return new RateRequest.Equipment();
    }

    /**
     * Create an instance of {@link RateRequest.Origin }
     * 
     */
    public RateRequest.Origin createRateRequestOrigin() {
        return new RateRequest.Origin();
    }

    /**
     * Create an instance of {@link RateRequest.Dest }
     * 
     */
    public RateRequest.Dest createRateRequestDest() {
        return new RateRequest.Dest();
    }

    /**
     * Create an instance of {@link RateRequest.Stops.Stop }
     * 
     */
    public RateRequest.Stops.Stop createRateRequestStopsStop() {
        return new RateRequest.Stops.Stop();
    }

    /**
     * Create an instance of {@link RateRequest.Commoditys.Commodity }
     * 
     */
    public RateRequest.Commoditys.Commodity createRateRequestCommoditysCommodity() {
        return new RateRequest.Commoditys.Commodity();
    }

    /**
     * Create an instance of {@link RateQuote.Equipment }
     * 
     */
    public RateQuote.Equipment createRateQuoteEquipment() {
        return new RateQuote.Equipment();
    }

    /**
     * Create an instance of {@link RateQuote.TariffAuth }
     * 
     */
    public RateQuote.TariffAuth createRateQuoteTariffAuth() {
        return new RateQuote.TariffAuth();
    }

    /**
     * Create an instance of {@link RateQuote.Leg }
     * 
     */
    public RateQuote.Leg createRateQuoteLeg() {
        return new RateQuote.Leg();
    }

    /**
     * Create an instance of {@link RateQuote.Charges.Charge }
     * 
     */
    public RateQuote.Charges.Charge createRateQuoteChargesCharge() {
        return new RateQuote.Charges.Charge();
    }

}
