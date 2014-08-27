
package eu.comvantage.domainconfiguration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für deleteDomainSystemParameter complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="deleteDomainSystemParameter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="param" type="{http://domainconfiguration.comvantage.eu/}domainSystemParameter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteDomainSystemParameter", propOrder = {
    "param"
})
public class DeleteDomainSystemParameter {

    protected DomainSystemParameter param;

    /**
     * Ruft den Wert der param-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DomainSystemParameter }
     *     
     */
    public DomainSystemParameter getParam() {
        return param;
    }

    /**
     * Legt den Wert der param-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DomainSystemParameter }
     *     
     */
    public void setParam(DomainSystemParameter value) {
        this.param = value;
    }

}
