
package eu.comvantage.domainconfiguration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für getDomainSPARULTemplateByIdResponse complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="getDomainSPARULTemplateByIdResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://domainconfiguration.comvantage.eu/}domainSPARULTemplate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDomainSPARULTemplateByIdResponse", propOrder = {
    "_return"
})
public class GetDomainSPARULTemplateByIdResponse {

    @XmlElement(name = "return")
    protected DomainSPARULTemplate _return;

    /**
     * Ruft den Wert der return-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DomainSPARULTemplate }
     *     
     */
    public DomainSPARULTemplate getReturn() {
        return _return;
    }

    /**
     * Legt den Wert der return-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DomainSPARULTemplate }
     *     
     */
    public void setReturn(DomainSPARULTemplate value) {
        this._return = value;
    }

}
