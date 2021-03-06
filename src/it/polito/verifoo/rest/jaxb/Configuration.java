//
// Questo file Ŕ stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrÓ persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.05.31 alle 08:25:09 PM CEST 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{}firewall"/>
 *         &lt;element ref="{}endhost"/>
 *         &lt;element ref="{}endpoint"/>
 *         &lt;element ref="{}antispam"/>
 *         &lt;element ref="{}cache"/>
 *         &lt;element ref="{}dpi"/>
 *         &lt;element ref="{}mailclient"/>
 *         &lt;element ref="{}mailserver"/>
 *         &lt;element ref="{}nat"/>
 *         &lt;element ref="{}vpnaccess"/>
 *         &lt;element ref="{}vpnexit"/>
 *         &lt;element ref="{}webclient"/>
 *         &lt;element ref="{}webserver"/>
 *         &lt;element ref="{}fieldmodifier"/>
 *       &lt;/choice>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "firewall",
    "endhost",
    "endpoint",
    "antispam",
    "cache",
    "dpi",
    "mailclient",
    "mailserver",
    "nat",
    "vpnaccess",
    "vpnexit",
    "webclient",
    "webserver",
    "fieldmodifier"
})
@XmlRootElement(name = "configuration")
public class Configuration {

    protected Firewall firewall;
    protected Endhost endhost;
    protected Endpoint endpoint;
    protected Antispam antispam;
    protected Cache cache;
    protected Dpi dpi;
    protected Mailclient mailclient;
    protected Mailserver mailserver;
    protected Nat nat;
    protected Vpnaccess vpnaccess;
    protected Vpnexit vpnexit;
    protected Webclient webclient;
    protected Webserver webserver;
    protected Fieldmodifier fieldmodifier;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;

    /**
     * Recupera il valore della proprietÓ firewall.
     * 
     * @return
     *     possible object is
     *     {@link Firewall }
     *     
     */
    public Firewall getFirewall() {
        return firewall;
    }

    /**
     * Imposta il valore della proprietÓ firewall.
     * 
     * @param value
     *     allowed object is
     *     {@link Firewall }
     *     
     */
    public void setFirewall(Firewall value) {
        this.firewall = value;
    }

    /**
     * Recupera il valore della proprietÓ endhost.
     * 
     * @return
     *     possible object is
     *     {@link Endhost }
     *     
     */
    public Endhost getEndhost() {
        return endhost;
    }

    /**
     * Imposta il valore della proprietÓ endhost.
     * 
     * @param value
     *     allowed object is
     *     {@link Endhost }
     *     
     */
    public void setEndhost(Endhost value) {
        this.endhost = value;
    }

    /**
     * Recupera il valore della proprietÓ endpoint.
     * 
     * @return
     *     possible object is
     *     {@link Endpoint }
     *     
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * Imposta il valore della proprietÓ endpoint.
     * 
     * @param value
     *     allowed object is
     *     {@link Endpoint }
     *     
     */
    public void setEndpoint(Endpoint value) {
        this.endpoint = value;
    }

    /**
     * Recupera il valore della proprietÓ antispam.
     * 
     * @return
     *     possible object is
     *     {@link Antispam }
     *     
     */
    public Antispam getAntispam() {
        return antispam;
    }

    /**
     * Imposta il valore della proprietÓ antispam.
     * 
     * @param value
     *     allowed object is
     *     {@link Antispam }
     *     
     */
    public void setAntispam(Antispam value) {
        this.antispam = value;
    }

    /**
     * Recupera il valore della proprietÓ cache.
     * 
     * @return
     *     possible object is
     *     {@link Cache }
     *     
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Imposta il valore della proprietÓ cache.
     * 
     * @param value
     *     allowed object is
     *     {@link Cache }
     *     
     */
    public void setCache(Cache value) {
        this.cache = value;
    }

    /**
     * Recupera il valore della proprietÓ dpi.
     * 
     * @return
     *     possible object is
     *     {@link Dpi }
     *     
     */
    public Dpi getDpi() {
        return dpi;
    }

    /**
     * Imposta il valore della proprietÓ dpi.
     * 
     * @param value
     *     allowed object is
     *     {@link Dpi }
     *     
     */
    public void setDpi(Dpi value) {
        this.dpi = value;
    }

    /**
     * Recupera il valore della proprietÓ mailclient.
     * 
     * @return
     *     possible object is
     *     {@link Mailclient }
     *     
     */
    public Mailclient getMailclient() {
        return mailclient;
    }

    /**
     * Imposta il valore della proprietÓ mailclient.
     * 
     * @param value
     *     allowed object is
     *     {@link Mailclient }
     *     
     */
    public void setMailclient(Mailclient value) {
        this.mailclient = value;
    }

    /**
     * Recupera il valore della proprietÓ mailserver.
     * 
     * @return
     *     possible object is
     *     {@link Mailserver }
     *     
     */
    public Mailserver getMailserver() {
        return mailserver;
    }

    /**
     * Imposta il valore della proprietÓ mailserver.
     * 
     * @param value
     *     allowed object is
     *     {@link Mailserver }
     *     
     */
    public void setMailserver(Mailserver value) {
        this.mailserver = value;
    }

    /**
     * Recupera il valore della proprietÓ nat.
     * 
     * @return
     *     possible object is
     *     {@link Nat }
     *     
     */
    public Nat getNat() {
        return nat;
    }

    /**
     * Imposta il valore della proprietÓ nat.
     * 
     * @param value
     *     allowed object is
     *     {@link Nat }
     *     
     */
    public void setNat(Nat value) {
        this.nat = value;
    }

    /**
     * Recupera il valore della proprietÓ vpnaccess.
     * 
     * @return
     *     possible object is
     *     {@link Vpnaccess }
     *     
     */
    public Vpnaccess getVpnaccess() {
        return vpnaccess;
    }

    /**
     * Imposta il valore della proprietÓ vpnaccess.
     * 
     * @param value
     *     allowed object is
     *     {@link Vpnaccess }
     *     
     */
    public void setVpnaccess(Vpnaccess value) {
        this.vpnaccess = value;
    }

    /**
     * Recupera il valore della proprietÓ vpnexit.
     * 
     * @return
     *     possible object is
     *     {@link Vpnexit }
     *     
     */
    public Vpnexit getVpnexit() {
        return vpnexit;
    }

    /**
     * Imposta il valore della proprietÓ vpnexit.
     * 
     * @param value
     *     allowed object is
     *     {@link Vpnexit }
     *     
     */
    public void setVpnexit(Vpnexit value) {
        this.vpnexit = value;
    }

    /**
     * Recupera il valore della proprietÓ webclient.
     * 
     * @return
     *     possible object is
     *     {@link Webclient }
     *     
     */
    public Webclient getWebclient() {
        return webclient;
    }

    /**
     * Imposta il valore della proprietÓ webclient.
     * 
     * @param value
     *     allowed object is
     *     {@link Webclient }
     *     
     */
    public void setWebclient(Webclient value) {
        this.webclient = value;
    }

    /**
     * Recupera il valore della proprietÓ webserver.
     * 
     * @return
     *     possible object is
     *     {@link Webserver }
     *     
     */
    public Webserver getWebserver() {
        return webserver;
    }

    /**
     * Imposta il valore della proprietÓ webserver.
     * 
     * @param value
     *     allowed object is
     *     {@link Webserver }
     *     
     */
    public void setWebserver(Webserver value) {
        this.webserver = value;
    }

    /**
     * Recupera il valore della proprietÓ fieldmodifier.
     * 
     * @return
     *     possible object is
     *     {@link Fieldmodifier }
     *     
     */
    public Fieldmodifier getFieldmodifier() {
        return fieldmodifier;
    }

    /**
     * Imposta il valore della proprietÓ fieldmodifier.
     * 
     * @param value
     *     allowed object is
     *     {@link Fieldmodifier }
     *     
     */
    public void setFieldmodifier(Fieldmodifier value) {
        this.fieldmodifier = value;
    }

    /**
     * Recupera il valore della proprietÓ id.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Imposta il valore della proprietÓ id.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Recupera il valore della proprietÓ name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietÓ name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietÓ description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Imposta il valore della proprietÓ description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
