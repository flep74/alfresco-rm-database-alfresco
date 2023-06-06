package dk.magenta.beans;


import dk.magenta.model.DatabaseModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.*;

import static dk.magenta.model.DatabaseModel.*;

public class PsycBean {

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private SiteService siteService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private NodeService nodeService;

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    private FileFolderService fileFolderService;

    public NodeRef getLibrary(String library) {
        NodeRef psycLibrary = siteService.getContainer("retspsyk", PROP_PSYC_LIBRARY);
        NodeRef child = nodeService.getChildByName(psycLibrary, ContentModel.ASSOC_CONTAINS, library);
        return child;
    }

    public void updateKonklusionTag(String selected_id, String newValue) {

        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_KONKLUSION_TAGS);
        List<ChildAssociationRef> children = nodeService.getChildAssocs(library);

        Iterator i = children.iterator();
        boolean found = false;

        while (i.hasNext() && !found) {
            ChildAssociationRef child = (ChildAssociationRef) i.next();
            String id = (String)nodeService.getProperty(child.getChildRef(), DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID);

            if (id.equals(selected_id)) {
                nodeService.setProperty(child.getChildRef(), DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME,newValue);
                found = true;
            }
        }
    }


    public String newItemInCategory(String category, String itemName) {

        String id = this.getNextPsycTypeId();
        NodeRef library = this.getLibrary(category);

        Map<QName, Serializable> props = new HashMap<>();
        props.put(QName.createQName(RMPSY_MODEL_URI, "id_" + category), id);
        props.put(QName.createQName(RMPSY_MODEL_URI, "name_" + category), itemName);

        nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, itemName),
                QName.createQName(RMPSY_MODEL_URI, category), props).getChildRef();

        return id; // return the id
    }

    public void newKonklusionTag(String newValue) {
        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_KONKLUSION_TAGS);

        String name = newValue;

        // sæt nyt id til antal children
        String id = this.getNextPsycTypeId();

        Map<QName, Serializable> props = new HashMap<>();
        props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
        props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

        nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
    }

    public void createDataForPSYCHTYPES() {

        System.out.println("opretter instrument: psykologiskeundersøgelsestyper");

        ArrayList<String> values = new ArrayList<>();
        values.add("Psykotiske sindslidelser eller tilstande");
        values.add("Grundlæggende begavelsesniveau");
        values.add("Personlighedsmæssige abnormaliteter");
        values.add("Organiske og neurologiske lidelser");
        values.add("Neuro-udviklingsforstyrrelser");

        values.add("Malingering (simulation og dissimulation");
        values.add("Risko for recidiv til ligeartet eller lignende kriminalitet (farlighed)");

        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_PSYCH_TYPE);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();
            System.out.println("name: " + name);
            System.out.println("id: " + id);

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }


    // Data for Psykiatriske interviews og ratingscales
    public void createDataForInterviewRating() {

        ArrayList<String> values = new ArrayList<>();
        values.add("PSE-10");
        values.add("EASE");
        values.add("PANSS");
        values.add("ASI");
        values.add("HCL:32");
        values.add("BDI-II");
        values.add("SCID-V");
        values.add("ADIS-V");
        values.add("Y-BOCS");
        values.add("HTQ");

        values.add("SCID-II og SCID-5-PD");
        values.add("OPD-2");
        values.add("PCL-R");
        values.add("PCL-SV");
        values.add("CAPP");
        values.add("ZANBPD");
        values.add("Hansson SAQ");
        values.add("Hostility TWQ");
        values.add("HADS");

        values.add("ABAS 3");
        values.add("VINELAND 3");
        values.add("ADOS 1 og 2");
        values.add("ADI-R");
        values.add("DIVA 1,2 og 5");
        values.add("AAI");
        values.add("SASB");
        values.add("PCL:YV");

        NodeRef library = this.getLibrary(PROP_PSYC_LIBRARY_INTERVIEWRATING);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }

    public void createDataForKognitivRating() {

        ArrayList<String> values = new ArrayList<>();
        values.add("WAIS-IV testbatteri");
        values.add("MoCA");
        values.add("MMSE");
        values.add("RIAS");
        values.add("WAIS-IV delprøver i udvalg");


        values.add("D2-test");
        values.add("Trail-Making test");
        values.add("RBANS delprøver i udvalg");
        values.add("RBANS testbatteri");
        values.add("ACE-III");

        values.add("SDMT");
        values.add("Rey's Auditory Verbal Learning Task");
        values.add("Street");
        values.add("Rey's Complex Figure Test");
        values.add("Sætningsspændvidde");

        values.add("Ordsprogsprøve");
        values.add("WMS-III testbatteri");
        values.add("WMS-III delprøver i udvalg");
        values.add("Kendte Ansigter");
        values.add("WAB testbatteri");

        values.add("WAB delprøver i udvalg");
        values.add("D-KEFS testbatteri");
        values.add("D-KEFS delprøver i udvalg");
        values.add("BADS testbatteri");
        values.add("BADS delprøver i udvalg");

        values.add("Stroop test");
        values.add("Wisconsin Card Sorting Test");
        values.add("IOWA Gambling Task v2");
        values.add("Tower of London");
        values.add("Ordmobilisering");

        values.add("RME-R");
        values.add("WAIS-III Billedordning");
        values.add("Brüne’s billedordning");
        values.add("Animated triangles");
        values.add("TASIT");

        values.add("Kognitive Estimater");

        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_KOGNITIV);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }

    public void createDataForImplicit() {

        ArrayList<String> values = new ArrayList<>();
        values.add("Rorschach-test");
        values.add("Ord-associationstest");
        values.add("Objektsorteringstest");
        values.add("TAT");
        values.add("Rotter’s Sætningsfuldendelsestest");

        NodeRef library = this.getLibrary(PROP_PSYC_LIBRARY_IMPLECITE);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }

    public void createDataForEksplicit() {

        ArrayList<String> values = new ArrayList<>();
        values.add("MMPI-2");
        values.add("MMPI-2-RF");
        values.add("SPQ");
        values.add("DES-II");
        values.add("PAI");

        values.add("PID-5");
        values.add("PiCD");
        values.add("MCMI-III");
        values.add("MCMI-IV");
        values.add("MACI");

        values.add("DAPP-BQ");
        values.add("SIPP-118");
        values.add("ICU");
        values.add("RAADS-R");
        values.add("DSQ-40");

        values.add("Neo-PI-3");

        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_EXPLICIT);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }

    public void createDataForMalingering() {

        ArrayList<String> values = new ArrayList<>();
        values.add("SIRS-2");
        values.add("M-FAST");
        values.add("SIMS");
        values.add("RFI");
        values.add("TOMM");

        values.add("RMT-W og RMT-F");
        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_MALERING);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }

    public void createDataForRisiko() {

        ArrayList<String> values = new ArrayList<>();
        values.add("SAPROF");
        values.add("FAM");
        values.add("HCR-20 v3");
        values.add("VRAG-R");
        values.add("VRAG");

        values.add("START");
        values.add("NAS-PI");
        values.add("ARMADILO-G");
        values.add("EARL 20B/21G");
        values.add("SAVRY");

        values.add("SAPROF-YV");
        values.add("STATIC-99-R");
        values.add("SVR-20 v2");
        values.add("RSVP");
        values.add("RSVP v2");

        values.add("SRP");
        values.add("SAM");
        values.add("SARA v3");
        values.add("ERASOR");
        values.add("ARMADILO-S");

        values.add("SNRF");
        values.add("F-FTAF");
        values.add("MLG");
        values.add("ERG-22+");
        values.add("VERA");

        values.add("TRAP-18");

        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_RISIKO);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }



    public void createDataForPsykMalering() {

        ArrayList<String> values = new ArrayList<>();
        values.add("Begrundet mistanke om dissimulation af psykisk lidelse");
        values.add("Vanskeligheder med optimalt samarbejde: Selvforskønnelse");
        values.add("Upåfaldende fremtræden / Ej taget stilling");
        values.add("Vanskeligheder med optimalt samarbejde: Selvforringelse");
        values.add("Begrundet mistanke om simulation af psykisk lidelse");

        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_PSYCH_MALERING);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }

    public void createDataForKonklusionTags() {

        ArrayList<String> values = new ArrayList<>();
        values.add("tag1");
        values.add("tag2");
        values.add("tag3");

        NodeRef library = this.getLibrary(DatabaseModel.PROP_PSYC_LIBRARY_KONKLUSION_TAGS);

        for (int i=0; i<=values.size()-1;i++ ) {
            String name = values.get(i);
            String id = this.getNextPsycTypeId();

            Map<QName, Serializable> props = new HashMap<>();
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_ID, id);
            props.put(DatabaseModel.PROP_ANVENDTUNDERSOEGELSESINST_NAME, name);

            nodeService.createNode(library, ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    DatabaseModel.TYPE_ANVENDTUNDERSOEGELSESINST, props).getChildRef();
        }
    }

    public void createAllData() {
        this.createRootFolders();
        this.createDataForPSYCHTYPES(); //ok
        this.createDataForInterviewRating(); // ok
        this.createDataForKognitivRating(); // ok
        this.createDataForImplicit(); // ok
        this.createDataForEksplicit(); // ok
        this.createDataForRisiko(); // ok
        this.createDataForMalingering(); // ok
        this.createDataForPsykMalering(); // ok
        this.createDataForKonklusionTags();
    }


    //todo lav mulighed for at senere at tilføje nye instrumenter

    private String getNextPsycTypeId() {

        // hent noderef for documentlibrary under retspsyk
        SiteInfo siteInfo = siteService.getSite("retspsyk");
        NodeRef documentlibrary = siteService.getContainer(siteInfo.getShortName(), "documentlibrary");

        int value;
        if (!nodeService.hasAspect(documentlibrary, ASPECT_PSYCDATA_COUNTER)) {
            System.out.println("der mangler en counter");
            Map<QName, Serializable> prop = new HashMap<>();
            prop.put(DatabaseModel.PROPQNAME_PSYCDATA_UNDERSOEGELSESTYPE_COUNTER, 1);
            nodeService.addAspect(documentlibrary, ASPECT_PSYCDATA_COUNTER,prop);
            value = 1;
        }
        else {
            value = (int)nodeService.getProperty(documentlibrary,DatabaseModel.PROPQNAME_PSYCDATA_UNDERSOEGELSESTYPE_COUNTER);
            value = value +1;
            nodeService.setProperty(documentlibrary,DatabaseModel.PROPQNAME_PSYCDATA_UNDERSOEGELSESTYPE_COUNTER, value);
        }

        System.out.println("hvad er value som kommer tilbage: " + value);

        return String.valueOf(value);
    }

    public void createRootFolders() {

        SiteInfo siteInfo = siteService.getSite("retspsyk");
        NodeRef psycLibrary = fileFolderService.create(siteInfo.getNodeRef(), PROP_PSYC_LIBRARY, ContentModel.TYPE_FOLDER).getNodeRef();

        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_PSYCH_TYPE, ContentModel.TYPE_FOLDER).getNodeRef();

        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_INTERVIEWRATING, ContentModel.TYPE_FOLDER).getNodeRef();
        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_KOGNITIV, ContentModel.TYPE_FOLDER).getNodeRef();
        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_IMPLECITE, ContentModel.TYPE_FOLDER).getNodeRef();
        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_EXPLICIT, ContentModel.TYPE_FOLDER).getNodeRef();
        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_MALERING, ContentModel.TYPE_FOLDER).getNodeRef();
        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_RISIKO, ContentModel.TYPE_FOLDER).getNodeRef();

        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_PSYCH_MALERING, ContentModel.TYPE_FOLDER).getNodeRef();
        fileFolderService.create(psycLibrary, PROP_PSYC_LIBRARY_KONKLUSION_TAGS, ContentModel.TYPE_FOLDER).getNodeRef();

        // init the primary key counter for the types
        nodeService.addAspect(psycLibrary, ASPECT_PSYCDATA_COUNTER,null);
        int counter = 0;
        nodeService.setProperty(psycLibrary, DatabaseModel.PROPQNAME_PSYCDATA_UNDERSOEGELSESTYPE_COUNTER, counter);
    }

    public void updateKonklusionText(NodeRef obs, String text) {
        nodeService.setProperty(obs, PROPQNAME_PSYCDATA_KONKLUSION_FREETEXT, text);
    }

    public String getKonklusionText(NodeRef obs) {
        return (String) nodeService.getProperty(obs, PROPQNAME_PSYCDATA_KONKLUSION_FREETEXT);
    }
}


