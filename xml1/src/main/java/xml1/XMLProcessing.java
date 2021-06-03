package xml1;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import xml1.persons.GenderType;
import xml1.persons.PersonType;
import xml1.persons.Persons;
import xml1.persons.RelatedPersonType;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class XMLProcessing {
    private static void createPersonType(Person person, Map<String, PersonType> xmlPersonsMap) {
        PersonType personType = new PersonType();
        personType.setId(person.getId());
        personType.setFirstname(person.getFirstName());
        personType.setSurname(person.getSecondName());
        personType.setGender(GenderType.fromValue(person.getGender()));
        xmlPersonsMap.put(person.getId(), personType);
    }

    private static void restoreFamily(
            PersonType personType, Map<String, PersonType> personTypeMap,
            Map<String, Person> map, Map<String, Person> nameMap) {
        if (!map.get(personType.getId()).getFather().equals("UNKNOWN")) {
            PersonType p = personTypeMap.get(nameMap.get(map.get(personType.getId()).getFather()).getId());
            RelatedPersonType type = new RelatedPersonType();
            type.setPersonId(p);
            personType.setFather(type);
        }

        if (!map.get(personType.getId()).getMother().equals("UNKNOWN")) {
            PersonType p = personTypeMap.get(nameMap.get(map.get(personType.getId()).getMother()).getId());
            RelatedPersonType type = new RelatedPersonType();
            type.setPersonId(p);
            personType.setMother(type);
        }

        if (!map.get(personType.getId()).getSpouseName().equals("NONE")) {
            PersonType p = personTypeMap.get(nameMap.get(map.get(personType.getId()).getSpouseName()).getId());
            RelatedPersonType type = new RelatedPersonType();
            type.setPersonId(p);
            personType.setSpouse(type);
        }

        map.get(personType.getId()).getChildren().forEach(s -> {
            PersonType p = personTypeMap.get(nameMap.get(s).getId());
            RelatedPersonType type = new RelatedPersonType();
            type.setPersonId(p);
            if (nameMap.get(s).getGender().equals("M")) {
                if (personType.getSons() == null) {
                    personType.setSons(new PersonType.Sons());
                }
                personType.getSons().getSon().add(type);
            }
            else {
                if (personType.getDaughters() == null) {
                    personType.setDaughters(new PersonType.Daughters());
                }
                personType.getDaughters().getDaughter().add(type);
            }
        });

        map.get(personType.getId()).getSiblings().forEach(s -> {
            PersonType p = personTypeMap.get(s);
            RelatedPersonType type = new RelatedPersonType();
            type.setPersonId(p);
            if (map.get(s).getGender().equals("M")) {
                if (personType.getBrothers() == null) {
                    personType.setBrothers(new PersonType.Brothers());
                }
                personType.getBrothers().getBrother().add(type);
            }
            else {
                if (personType.getSisters() == null) {
                    personType.setSisters(new PersonType.Sisters());
                }
                personType.getSisters().getSister().add(type);
            }
        });
    }
    public static void writeDataToFile(Map<String, Person> map, Map<String, Person> nameMap, String outPath, String xsdPath) {
        Persons persons = new Persons();
        Map<String, PersonType> personsMap = new TreeMap<>();

        map.values().forEach(x -> createPersonType(x, personsMap));

        personsMap.values().forEach(x -> restoreFamily(x, personsMap, map, nameMap));

        persons.getPerson().addAll(personsMap.values());

        try {
            JAXBContext jc =
                    JAXBContext.newInstance(Persons.class);
            Marshaller writer = null;
            writer = jc.createMarshaller();
            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File schemaFile = new File(xsdPath);
            writer.setSchema(schemaFactory.newSchema(schemaFile));
            writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            writer.marshal(persons, new File(outPath));
        } catch (SAXException | JAXBException e) {
            e.printStackTrace();
        }
    }
    public static void writeXSLT(String outPath, String xmlPath, String xsdPath, String xslPath) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File schemaFile = new File(xsdPath);

            docBuilderFactory.setSchema(schemaFactory.newSchema(schemaFile));

            docBuilderFactory.setNamespaceAware(true);
            Document doc =
                    docBuilderFactory.newDocumentBuilder().parse(xmlPath);
            StreamSource styleSheet = new StreamSource(new File(xslPath));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer(styleSheet);
            transformer.transform(new DOMSource(doc),
                    new StreamResult(new FileOutputStream(outPath)));
        } catch (SAXException
                | IOException
                | ParserConfigurationException
                | TransformerException e) {
            assert false;
        }

    }
}
