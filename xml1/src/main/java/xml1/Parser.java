package xml1;

import javax.xml.namespace.QName;
import javax.xml.stream.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    public static void restore1(Map<String, Person> map, Map<String, Person> nameMap, Map<String, String> idsToRemove, boolean byNames) throws FileNotFoundException, XMLStreamException {
        Map<String, String> fullNames = new TreeMap<>();
        if (byNames) {
            map.values().forEach(person -> {
                String name = person.getFirstName() + " " + person.getSecondName();
                if (fullNames.containsKey(name)) {
                    idsToRemove.put(fullNames.get(name), name);
                    idsToRemove.put(person.getId(), name);
                }
                fullNames.put(name, person.getId());
            });
            idsToRemove.keySet().forEach(map::remove);
            map.values().forEach(person ->
                    nameMap.put(person.getFirstName() + " " + person.getSecondName(), person));
        }
        XMLInputFactory streamFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = streamFactory.createXMLStreamReader(
                new FileInputStream(new File("people.xml")));
        String firstName = null;
        String secondName = null;
        String id = null;
        String gender = null;
        Set<String> parents = new TreeSet<>();
        String mother = null;
        String father = null;
        Set<String> siblings = new TreeSet<>();
        Set<String> sisters = new TreeSet<>();
        Set<String> brothers = new TreeSet<>();
        String spouse = null;
        Set<String> children = new TreeSet<>();
        Set<String> daughters = new TreeSet<>();
        Set<String> sons = new TreeSet<>();
        Integer childrenNum = null;
        Integer siblingsNum = null;
        String spouseGender = null;
        String spouseName = null;
        String lastStart = "";
        for (; reader.hasNext(); reader.next()) {
            int eventType = reader.getEventType();
            if (eventType == XMLStreamConstants.START_ELEMENT) {
                lastStart = reader.getLocalName();
                switch (lastStart) {
                    case "person" -> {
                        if (reader.getAttributeCount() > 0) {
                            QName name = reader.getAttributeName(0);
                            if (name.toString().equals("id")) {
                                id = reader.getAttributeValue(0);
                            } else if (name.toString().equals("name")) {
                                String[] s = reader.getAttributeValue(0).strip().split("\\s+");
                                firstName = s[0];
                                secondName = s[1];
                            }
                        }
                    }
                    case "id" -> {
                        if (reader.getAttributeCount() > 0) {
                            id = reader.getAttributeValue(0);
                        }
                    }
                    case "gender" -> {
                        if (reader.getAttributeCount() > 0) {
                            if (reader.getAttributeValue(0).equals("male")) {
                                gender = "M";
                            } else {
                                gender = "F";
                            }
                        }
                    }
                    case "firstname" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            firstName = s;
                        }
                    }
                    case "surname" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            secondName = s;
                        }
                    }
                    case "parent" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            parents.add(s);
                        }
                    }
                    case "spouce" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            spouseName = spouseFormat(s);
                        } else {
                            spouseName = "NONE";
                        }
                    }
                    case "wife" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            spouse = s;
                            spouseGender = "F";
                        }
                    }
                    case "husband" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            spouse = s;
                            spouseGender = "M";
                        }
                    }
                    case "daughter" -> daughters.add(reader.getAttributeValue(0));
                    case "son" -> sons.add(reader.getAttributeValue(0));
                    case "siblings" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            String[] siblingsList = s.strip().split("\\s+");
                            siblings.addAll(Arrays.asList(siblingsList));
                        }
                    }
                    case "children-number" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            childrenNum = Integer.parseInt(s);
                        }
                    }
                    case "siblings-number" -> {
                        String s = reader.getAttributeValue(0);
                        if (s != null) {
                            siblingsNum = Integer.parseInt(s);
                        }
                    }
                }
            } else if (eventType == XMLStreamConstants.CHARACTERS) {
                switch (lastStart) {
                    case "first", "firstname" -> {
                        String text = reader.getText().replaceAll("\\s+", "");
                        if (!text.isEmpty()) {
                            firstName = text;
                        }
                    }
                    case "family", "family-name" -> {
                        String text = reader.getText().replaceAll("\\s+", "");
                        if (!text.isEmpty()) {
                            secondName = text;
                        }
                    }
                    case "gender" -> {
                        String text = reader.getText().replaceAll("\\s+", "");
                        if (!text.isEmpty()) {
                            gender = text;
                        }
                    }
                    case "parent" -> {
                        String text = reader.getText().replaceAll("\\s+", "");
                        if (!text.isEmpty()) {
                            parents.add(text);
                        }
                    }
                    case "father" -> {
                        String text = reader.getText().strip();
                        if (!text.isEmpty()) {
                            String[] fatherName = text.split("\\s+");
                            father = fatherName[0] + " " + fatherName[1];
                        }
                    }
                    case "mother" -> {
                        String text = reader.getText().strip();
                        if (!text.isEmpty()) {
                            String[] name = text.split("\\s+");
                            mother = name[0] + " " + name[1];
                        }
                    }
                    case "child" -> {
                        String text = reader.getText().strip();
                        if (!text.isEmpty()) {
                            String[] name = text.split("\\s+");
                            children.add(name[0] + " " + name[1]);
                        }
                    }
                    case "brother" -> {
                        String text = reader.getText().strip();
                        if (!text.isEmpty()) {
                            String[] name = text.split("\\s+");
                            brothers.add(name[0] + " " + name[1]);
                        }
                    }
                    case "sister" -> {
                        String text = reader.getText().strip();
                        if (!text.isEmpty()) {
                            String[] name = text.split("\\s+");
                            sisters.add(name[0] + " " + name[1]);
                        }
                    }
                }
            } else if (eventType == XMLStreamConstants.END_ELEMENT) {
                if (reader.getLocalName().equals("person")) {
                    //пол по дочерям/сыновьям
                    daughters.forEach(daughter -> {
                        if (!map.containsKey(daughter)) {
                            map.put(daughter, new Person(daughter));
                        }
                        map.get(daughter).setGender("F");
                    });
                    sons.forEach(son -> {
                        if (!map.containsKey(son)) {
                            map.put(son, new Person(son));
                        }
                        map.get(son).setGender("M");
                    });
                    Person p = null;
                    if (id != null && !byNames) {
                        if (!map.containsKey(id)) {
                            map.put(id, new Person(id));
                        }
                        p = map.get(id);
                    } else if (firstName != null && secondName != null && byNames) {
                        p = nameMap.get(firstName + " " + secondName);

                        final String finalFirstName = firstName;
                        final String finalSecondName = secondName;
                        parents.forEach(par -> {
                            if (!par.equals("UNKNOWN") && map.containsKey(par)) {
                                map.get(par).getChildren().add(finalFirstName + " " + finalSecondName);
                            }
                        });
                    }
                    if (p != null) {
                        if (firstName != null) {
                            firstName = firstName.replaceAll("\\s+", "");
                            p.setFirstName(firstName);
                        }
                        if (secondName != null) {
                            secondName = secondName.replaceAll("\\s+", "");
                            p.setSecondName(secondName);
                        }
                        if (gender != null) {
                            p.setGender(gender);
                        }
                        if (spouseName != null) {
                            p.setSpouseName(spouseName);
                        }
                        if (spouse != null) {
                            if (!byNames) {
                                if (!map.containsKey(spouse)) {
                                    map.put(spouse, new Person(spouse));
                                }
                            }
                            p.setSpouse(spouse);
                            p.setSpouseGender(spouseGender);
                            if (byNames) {
                                if (map.containsKey(spouse)) {
                                    map.get(spouse).setSpouseName(p.getFirstName() + " " + p.getSecondName());
                                    p.setSpouseName(map.get(spouse).getFirstName() + " " + map.get(spouse).getSecondName());
                                }
                            }
                        }
                        if (siblingsNum != null) {
                            p.setSiblingsNum(siblingsNum);
                        }
                        if (childrenNum != null) {
                            p.setChildrenNum(childrenNum);
                        }
                        if (mother != null) {
                            p.setMother(mother);
                        }
                        if (father != null) {
                            p.setFather(father);
                        }
                        p.getParents().addAll(parents);
                        p.getSons().addAll(sons);
                        p.getDaughters().addAll(daughters);
                        p.getSisters().addAll(sisters);
                        p.getBrothers().addAll(brothers);
                        p.getChildren().addAll(children);
                        final String finalId = id;
                        final Person finalPerson = p;
                        siblings.forEach(sibling -> {
                            finalPerson.getSiblings().add(sibling);
                            Person pl;
                            if (!map.containsKey(sibling)) {
                                map.put(sibling, new Person(sibling));
                            }
                            pl = map.get(sibling);
                            if (finalId != null) {
                                pl.getSiblings().add(finalId);
                            }
                        });
                    }
                    firstName = null;
                    secondName = null;
                    id = null;
                    gender = null;
                    parents = new TreeSet<>();
                    mother = null;
                    father = null;
                    siblings = new TreeSet<>();
                    sisters = new TreeSet<>();
                    brothers = new TreeSet<>();
                    spouse = null;
                    children = new TreeSet<>();
                    daughters = new TreeSet<>();
                    sons = new TreeSet<>();
                    childrenNum = null;
                    siblingsNum = null;
                    spouseGender = null;
                    spouseName = null;
                }
            }
        }
        reader.close();
        if (!byNames) {
            map.values().forEach(person -> {
                person.getParents().forEach(par -> {
                    if (!par.equals("UNKNOWN")) {
                        map.get(par).getChildren().add(person.getFirstName() + " " + person.getSecondName());
                        person.getSiblings().forEach(sib -> {
                            if (map.containsKey(sib)) {
                                map.get(par).getChildren()
                                        .add(map.get(sib).getFirstName() + " " + map.get(sib).getSecondName());
                            }
                        });
                        map.get(par).getChildren().addAll(person.getSisters());
                        map.get(par).getChildren().addAll(person.getBrothers());
                    }
                });
            });
        }
        idsToRemove.keySet().forEach(map::remove);
    }

    private static String spouseFormat(String s) {
        String spouseName;
        spouseName = s.strip();
        String[] fullName = spouseName.split("\\s+");
        if (fullName.length > 1) {
            spouseName = fullName[0] + " " + fullName[1];
        }
        return spouseName;
    }

    public static void restore2(Map<String, Person> map, Map<String, Person> nameMap, Map<String, String> idsToRemove) {
        map.values().forEach(person -> {
            if (person.getSpouse() != null && map.containsKey(person.getSpouse())) {
                map.get(person.getSpouse()).setSpouseName(person.getFirstName() + " " + person.getSecondName());
                person.setSpouseName(
                        map.get(person.getSpouse()).getFirstName()
                                + " "
                                + map.get(person.getSpouse()).getSecondName());
            } else if (person.getSpouse() != null && !map.containsKey(person.getSpouse())) {
                person.setSpouseName("UNKNOWN");
                person.setSpouse("NONE");
            }
            if (person.getMother() != null) {
                if (nameMap.containsKey(person.getMother())) {
                    person.getParents().add(nameMap.get(person.getMother()).getId());
                }
            }
            if (person.getFather() != null) {
                if (nameMap.containsKey(person.getFather())) {
                    person.getParents().add(nameMap.get(person.getFather()).getId());
                }
            }
            person.getSisters().forEach(sis -> {
                if (nameMap.containsKey(sis)) {
                    person.getSiblings().add(nameMap.get(sis).getId());
                }
            });
            person.getBrothers().forEach(bro -> {
                if (nameMap.containsKey(bro)) {
                    person.getSiblings().add(nameMap.get(bro).getId());
                }
            });
            person.getSons().forEach(s -> {
                if (map.containsKey(s)) {
                    person.getChildren().add(map.get(s).getFirstName() + " " + map.get(s).getSecondName());
                }
            });
            person.getDaughters().forEach(d -> {
                if (map.containsKey(d)) {
                    person.getChildren().add(map.get(d).getFirstName() + " " + map.get(d).getSecondName());
                }
            });
        });
        map.values().forEach(person -> {
            if (person.getParents().size() == 1) {
                person.setFather("UNKNOWN");
                person.setMother("UNKNOWN");
            }
            person.getParents().forEach(parent -> {
                if (map.containsKey(parent)) {
                    String name = map.get(parent).getFirstName() + " " + map.get(parent).getSecondName();
                    if (map.get(parent).getGender().equals("F")) {
                        person.setMother(name);
                    } else {
                        person.setFather(name);
                    }
                }
            });
            if (person.getFather() == null) {
                person.setFather("UNKNOWN");
            }
            if (person.getMother() == null) {
                person.setMother("UNKNOWN");
            }
        });
        map.values().forEach(person -> {
            List<String> remList = new ArrayList<>();
            person.getChildren().forEach(c -> {
                if (!nameMap.containsKey(c)) {
                    remList.add(c);
                    person.setChildrenNum(person.getChildrenNum() - 1);
                }
            });
            remList.forEach(n -> person.getChildren().remove(n));
            remList.clear();
            person.getSiblings().forEach(c -> {
                if (!map.containsKey(c)) {
                    remList.add(c);
                    person.setSiblingsNum(person.getSiblingsNum() - 1);
                }
            });
            remList.forEach(n -> person.getSiblings().remove(n));
            if (!nameMap.containsKey(person.getMother())) {
                person.setMother("UNKNOWN");
            }
            if (!nameMap.containsKey(person.getFather())) {
                person.setFather("UNKNOWN");
            }
            if (!nameMap.containsKey(person.getSpouseName())) {
                person.setSpouseName("NONE");
            }
        });
    }
}
