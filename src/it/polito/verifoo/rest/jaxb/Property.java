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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Property complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Property">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="HTTPDefinition" type="{}HTTPDefinition" minOccurs="0"/>
 *         &lt;element name="POP3Definition" type="{}POP3Definition" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{}P-Name" />
 *       &lt;attribute name="graph" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="src" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dst" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lv4proto" type="{}L4ProtocolTypes" default="ANY" />
 *       &lt;attribute name="src_port" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dst_port" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isSat" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property", propOrder = {
    "httpDefinition",
    "pop3Definition"
})
public class Property {

    @XmlElement(name = "HTTPDefinition")
    protected HTTPDefinition httpDefinition;
    @XmlElement(name = "POP3Definition")
    protected POP3Definition pop3Definition;
    @XmlAttribute(name = "name", required = true)
    protected PName name;
    @XmlAttribute(name = "graph", required = true)
    protected long graph;
    @XmlAttribute(name = "src", required = true)
    protected String src;
    @XmlAttribute(name = "dst", required = true)
    protected String dst;
    @XmlAttribute(name = "lv4proto")
    protected L4ProtocolTypes lv4Proto;
    @XmlAttribute(name = "src_port")
    protected String srcPort;
    @XmlAttribute(name = "dst_port")
    protected String dstPort;
    @XmlAttribute(name = "isSat")
    protected Boolean isSat;

    /**
     * Recupera il valore della proprietÓ httpDefinition.
     * 
     * @return
     *     possible object is
     *     {@link HTTPDefinition }
     *     
     */
    public HTTPDefinition getHTTPDefinition() {
        return httpDefinition;
    }

    /**
     * Imposta il valore della proprietÓ httpDefinition.
     * 
     * @param value
     *     allowed object is
     *     {@link HTTPDefinition }
     *     
     */
    public void setHTTPDefinition(HTTPDefinition value) {
        this.httpDefinition = value;
    }

    /**
     * Recupera il valore della proprietÓ pop3Definition.
     * 
     * @return
     *     possible object is
     *     {@link POP3Definition }
     *     
     */
    public POP3Definition getPOP3Definition() {
        return pop3Definition;
    }

    /**
     * Imposta il valore della proprietÓ pop3Definition.
     * 
     * @param value
     *     allowed object is
     *     {@link POP3Definition }
     *     
     */
    public void setPOP3Definition(POP3Definition value) {
        this.pop3Definition = value;
    }

    /**
     * Recupera il valore della proprietÓ name.
     * 
     * @return
     *     possible object is
     *     {@link PName }
     *     
     */
    public PName getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietÓ name.
     * 
     * @param value
     *     allowed object is
     *     {@link PName }
     *     
     */
    public void setName(PName value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietÓ graph.
     * 
     */
    public long getGraph() {
        return graph;
    }

    /**
     * Imposta il valore della proprietÓ graph.
     * 
     */
    public void setGraph(long value) {
        this.graph = value;
    }

    /**
     * Recupera il valore della proprietÓ src.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrc() {
        return src;
    }

    /**
     * Imposta il valore della proprietÓ src.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrc(String value) {
        this.src = value;
    }

    /**
     * Recupera il valore della proprietÓ dst.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDst() {
        return dst;
    }

    /**
     * Imposta il valore della proprietÓ dst.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDst(String value) {
        this.dst = value;
    }

    /**
     * Recupera il valore della proprietÓ lv4Proto.
     * 
     * @return
     *     possible object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public L4ProtocolTypes getLv4Proto() {
        if (lv4Proto == null) {
            return L4ProtocolTypes.ANY;
        } else {
            return lv4Proto;
        }
    }

    /**
     * Imposta il valore della proprietÓ lv4Proto.
     * 
     * @param value
     *     allowed object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public void setLv4Proto(L4ProtocolTypes value) {
        this.lv4Proto = value;
    }

    /**
     * Recupera il valore della proprietÓ srcPort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcPort() {
        return srcPort;
    }

    /**
     * Imposta il valore della proprietÓ srcPort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcPort(String value) {
        this.srcPort = value;
    }

    /**
     * Recupera il valore della proprietÓ dstPort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDstPort() {
        return dstPort;
    }

    /**
     * Imposta il valore della proprietÓ dstPort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDstPort(String value) {
        this.dstPort = value;
    }

    /**
     * Recupera il valore della proprietÓ isSat.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSat() {
        return isSat;
    }

    /**
     * Imposta il valore della proprietÓ isSat.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSat(Boolean value) {
        this.isSat = value;
    }

}
