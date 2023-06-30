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
        values.add("Farlighed (risiko for recidiv til ligeartet eller lignende kriminalitet)");



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
        values.add("PCL:SV");
        values.add("CAPP");
        values.add("ZAN-BPD");
        values.add("Hansson SAQ");
        values.add("Hostility TWQ");
        values.add("HADS");

        values.add("ABAS 3");
        values.add("VINELAND 3");
        values.add("ADOS 1 og 2");
        values.add("ADI-R");
        values.add("DIVA 1, 2 og 5");
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
        values.add("WAIS-III testbatteri");
        values.add("WAIS-III delprøver");
        values.add("WAIS-IV testbatteri");
        values.add("WAIS-IV delprøver");

        values.add("RAVENS 2 screening");
        values.add("Leither 3 screening");
        values.add("RAIS screening");
        values.add("SON-R 6.40 screening");

        values.add("MMSE testbatteri");
        values.add("ACE-III testbatteri");
        values.add("MoCA testbatteri");
        values.add("RUDAS testbatteri");

        values.add("CTNB testbatteri");
        values.add("CTNB delprøver");
        values.add("WAB testbatteri");
        values.add("WAB delprøver");

        values.add("RBANS testbatteri");
        values.add("RBANS delprøver");
        values.add("WMS-III testbatteri");
        values.add("WMS-III delprøver");

        values.add("D-KEFS testbatteri");
        values.add("D-KEFS delprøver");
        values.add("BADS testbatteri");
        values.add("BADS delprøver");

//        her skifter det

        values.add("RME-R");
        values.add("Brüne's Billedordning");
        values.add("Animerede trekanter");
        values.add("TASIT");

        values.add("Ordmobilisering");
        values.add("Kognitive Estimater");
        values.add("Street test");
        values.add("Kendte Ansigter");

        values.add("STROOP test");
        values.add("Tower of London");
        values.add("IOWA Gambling Task v2");
        values.add("Wisconsin Card Sorting Test");

        values.add("RAVLT");
        values.add("RCFT");
        values.add("Ordsprogsprøve");
        values.add("Sætningsspændvidde");

        values.add("SDMT");
        values.add("D2/D2-R test");
        values.add("Kategoristyret billedordning");
        values.add("Trail-Making test");

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
        values.add("Rorschach test (CS og CS-R)");
        values.add("Ord-associationstest");
        values.add("Objektsorteringstest");
        values.add("TAT");
        values.add("Rotter’s Sætningsfuldendelsestest");
        values.add("AAAPS");

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
        values.add("MCMI-III");
        values.add("MMPI-A");

        values.add("MMPI-2 RF");
        values.add("MCMI-IV");
        values.add("MMPI-A RF");

        values.add("SPQ");
        values.add("SIPP-118");
        values.add("MACI");

        values.add("PAI");
        values.add("DIP-Q");
        values.add("ICU");

        values.add("DES-II");
        values.add("PID-5");
        values.add("BRIEF og BRIEF-2");

        values.add("PiCD");
        values.add("RAADS-R");

        values.add("DAPP-BQ");
        values.add("SRS-2");

        values.add("DSQ-40");

        values.add("Neo-PI-R og Neo-PI-3");

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
        values.add("M-FAST");
        values.add("SIRS-2");

        values.add("TOMM");
        values.add("SIMS");
        values.add("MMPI-2 / MMPI-2 RF");

        values.add("RMT-W og RMT-F");
        values.add("MENT");
        values.add("PAI");

        values.add("VSVT");
        values.add("ADI");
        values.add("MCMI-III / MCMI-IV");

        values.add("RFI");




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
        values.add("HCR-20-v2");
        values.add("HCR-20 v3");
        values.add("SAPROV v2");
        values.add("FAM");

        values.add("SVR-20 v1");
        values.add("SVR-20 v2");
        values.add("RSVP v1");
        values.add("RSVP v2");

        values.add("SARA v3");
        values.add("PATRIARCH v2");
        values.add("SAM");
        values.add("SRP");

        values.add("ARMADILO-G");
        values.add("ARMADILO-S");
        values.add("M-TTAF");
        values.add("NFRA");

        values.add("MLG");
        values.add("ERG-22+");
        values.add("VERA-2R");
        values.add("TRAP-18");

        values.add("EARL 20B/21G");
        values.add("SAVRY");
        values.add("SAPROV:YV");

        values.add("J-SOAP II");
        values.add("ERASOR v2");
        values.add("MEGA");

        values.add("VRAG");
        values.add("VRAG-R");
        values.add("STATIC-99-R");
        values.add("STATIC-2000");

        values.add("BVC");
        values.add("START");
        values.add("V-RISK 10");
        values.add("VRT");


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
        values.add("Ingen eller uafklaret stillingtagen");
        values.add("Neutral fremtræden: Upåfaldende selvfremstilling");

        values.add("Forvrænget selvfremstilling: Selvforskønnende idyllisering ");
        values.add("Forvrænget selvfremstilling: Selvforringende dramatisering");

        values.add("Påvist dissimulation: Skjult psykisk lidelse");
        values.add("Påvist simulation: Fingeret psykisk lidelse");

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
        values.add("Neurodevelopment disorders");
        values.add("Disorders of bodily distress or bodily experience");
        values.add("Pseudo-psykopatisk skizofreni");

        values.add("Schizophrenia and other primary psychotic disorders");
        values.add("Disorders due to substance use or addictive behaviors");
        values.add("R41.8 Inferioritas intellektualis (70-85)");

        values.add("Catatonia");
        values.add("Impulse control disorders");
        values.add("Påvist kromosomafvigelse");

        values.add("Mood disorders");
        values.add("Distruptive behavior or dissocial disorders");
        values.add("Auditory Processing Disorder (APD)");

        values.add("Anxiety and fear-related disorders");
        values.add("Personality disorders and related traits");
        values.add("Non-verbal Learning Disorder (NLD)");

        values.add("Obsessive-compulsive and related disorders");
        values.add("Paraphilic disorders");
        values.add("PCL-psykopati (>=27 eller >= 18)");

        values.add("Disorders specifically associated with stress");
        values.add("Factitious disorders");
        values.add("Patologisk (malign) narcissisme");

        values.add("Dissociative disorders");
        values.add("Neurocognitive disorders");
        values.add("Z65.1 Fængsling eller indespærring");

        values.add("Feeding or eating disorders");
        values.add("Mental or behavioral disorders associated with pregnancy, childbirth or the puerperium");
        values.add("Z76.4 Simulation");

        values.add("Elimination disorders");
        values.add("Secondary mental or behavioral syndroms associated with disorders or diseases classified elswhere");
        values.add("Uafklaret psykopatologi");


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
        this.createDataForMalingering(); // ok
        this.createDataForRisiko(); // ok
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


