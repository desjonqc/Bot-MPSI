package com.cegesoft.prepa.godfather;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GFDistributor {

    public static void main(String[] args) throws IOException {
        JsonObject classes;
        File file = new File("distributor/classes.json");
        if (file.exists()) {
            classes = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
        } else
            return;

        Scanner scanner = new Scanner(System.in);

        List<Student> godfathers = new ArrayList<>();
        List<Student> godsons = new ArrayList<>();
        for (JsonElement element : classes.get("mp1").getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Student student = new Student(object.get("name").getAsString(), object.get("surname").getAsString(),
                    "MP 1", object.get("rank").getAsString(), object.get("sex").getAsString(), object.get("from").getAsString());
            godfathers.add(student);
        }

        for (JsonElement element : classes.get("mp2").getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Student student = new Student(object.get("name").getAsString(), object.get("surname").getAsString(),
                    "MP 2", object.get("rank").getAsString(), object.get("sex").getAsString(), object.get("from").getAsString());
            godfathers.add(student);
        }

        for (JsonElement element : classes.get("mp*").getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Student student = new Student(object.get("name").getAsString(), object.get("surname").getAsString(),
                    "MP*");
            godfathers.add(student);
        }

        for (JsonElement element : classes.get("mpsi").getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Student student = new Student(object.get("name").getAsString(), object.get("surname").getAsString(),
                    object.get("class").getAsString());
            godsons.add(student);
        }

        System.out.println("Godfathers: " + godfathers.size());
        System.out.println("Godsons: " + godsons.size());
        System.out.println("Total: " + (godfathers.size() + godsons.size()));

        double ratio = godsons.size() * 1.0d / godfathers.size();
        System.out.println("Ratio: " + ratio);

        GodFatherFileHandler fileHandler = new GodFatherFileHandler("distributor");
        List<GFGroup> groups = fileHandler.load();
        System.out.println("Groups: " + groups.size());

        ArrayList<FinalGFGroup> finalGFGroups = new ArrayList<>();

        for (GFGroup group : groups) {
            FinalGFGroup finalGFGroup = new FinalGFGroup();
            for (String gf : group.getGf()) {
                boolean found = false;
                for (Student godfather : godfathers) {
                    if (compare(gf, godfather.name) && compare(gf, godfather.surname) && !godfather.taken) {
                        finalGFGroup.godfathers.add(godfather);
                        godfather.taken = true;
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("GodFather not found: " + gf);
                    for (Student godfather : godfathers) {
                        if ((compare(gf, godfather.name) || compare(gf, godfather.surname)) && !godfather.taken) {
                            System.out.println("Found: " + godfather.name + " " + godfather.surname);
                            System.out.println("Is it him? (y/n)");
                            String answer = scanner.nextLine();
                            if (answer.equalsIgnoreCase("y")) {
                                finalGFGroup.godfathers.add(godfather);
                                godfather.taken = true;
                                break;
                            }
                        }
                    }
                }
            }
            for (String gs : group.getGs()) {
                boolean found = false;
                for (Student godson : godsons) {
                    if (compare(gs, godson.name) && compare(gs, godson.surname) && !godson.taken) {
                        finalGFGroup.godsons.add(godson);
                        godson.taken = true;
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("GodSon not found: " + gs);
                    for (Student godson: godsons) {
                        if ((compare(gs, godson.name) || compare(gs, godson.surname)) && !godson.taken) {
                            System.out.println("Found: " + godson.name + " " + godson.surname);
                            System.out.println("Is it him? (y/n)");
                            String answer = scanner.nextLine();
                            if (answer.equalsIgnoreCase("y")) {
                                finalGFGroup.godsons.add(godson);
                                godson.taken = true;
                                break;
                            }
                        }
                    }
                }
            }
            finalGFGroups.add(finalGFGroup);
        }
        System.out.println("FinalGroups: " + finalGFGroups.size());
        System.out.println("GodFather left overs: " + godfathers.stream().filter(gf -> !gf.taken).count());

        System.out.println("Creating new groups...");

        Collections.shuffle(godfathers);

        int count = 0;
        FinalGFGroup lastGroup = null;
        for (Student student : godfathers) {
            if (!student.taken) {
                if (lastGroup == null || lastGroup.godfathers.size() >= 3) {
                    count++;
                    lastGroup = new FinalGFGroup();
                    finalGFGroups.add(lastGroup);
                }
                lastGroup.godfathers.add(student);
                student.taken = true;
            }
        }
        if (lastGroup != null && lastGroup.godfathers.size() == 1) {
            finalGFGroups.remove(lastGroup);
            finalGFGroups.get(finalGFGroups.size() - 1).godfathers.add(lastGroup.godfathers.get(0));
            count--;
        }
        System.out.println("New groups created: " + count);

        System.out.println("GodSon left overs: " + godsons.stream().filter(gs -> !gs.taken).count());
        System.out.println("Distributing left overs...");

        Collections.shuffle(godsons);
        int i = 0;

        for (Student student : godsons) {
            if (!student.taken) {
                while (i != finalGFGroups.size() -1 && finalGFGroups.get(i).godsons.size() >= finalGFGroups.get(i).godfathers.size()) {
                    i++;
                }
                FinalGFGroup group = finalGFGroups.get(i);
                group.godsons.add(student);
                student.taken = true;
            }
        }

        System.out.println("GodSon left overs: " + godsons.stream().filter(gs -> !gs.taken).count());
        System.out.println("Done !");

        System.out.println("BUS dealing...");
        Bus[] buses = new Bus[] {new Bus(79), new Bus(62), new Bus(54)};
        Collections.shuffle(finalGFGroups);

        List<FinalGFGroup> rest = new ArrayList<>(finalGFGroups);
        int busResult = dispatchBuses(buses, rest);
        System.out.println("Bus result: " + busResult);

        finalGFGroups.stream().filter(g -> !rest.contains(g)).forEach(g -> g.inBus = true);

        System.out.println("Done !");
        for (int j = 0; j < 3; j++) {
            System.out.println("Bus " + (j + 1) + ": " + buses[j].groups.size() + " groups" + " (" + buses[j].getPlacesLeft() + " places left)");
            System.out.println("Saving to clipboard...");
            StringSelection selection = new StringSelection(parseToLatex(buses[j].groups));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            System.out.println("Saved to clipboard !");
            System.out.println("Press enter to continue...");
            scanner.nextLine();
        }

        System.out.println("Groups left overs: " + rest.size());
        System.out.println("Saving to clipboard...");
        StringSelection selection = new StringSelection(parseToLatex(finalGFGroups.stream().filter(g -> !g.inBus).collect(Collectors.toList())));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        System.out.println("Saved to clipboard !");
    }

    private static boolean compare(String full, String contains) {
        String normalizedFull = StringUtils.stripAccents(full.toLowerCase()).replaceAll("[ \\-]", "");
        String normalizedContains = StringUtils.stripAccents(contains.toLowerCase()).replaceAll("[ \\-]", "");
        return normalizedFull.contains(normalizedContains);
    }

    private static String parseToLatex(List<FinalGFGroup> groups) {
        StringBuilder builder = new StringBuilder();
        for (FinalGFGroup group : groups) {
            builder.append(group.toLatexTable());
        }
        return builder.toString();
    }

    public static int dispatchBuses(Bus[] buses, List<FinalGFGroup> groups) {
        if (groups.size() == 0) {
            return 0;
        }
        boolean busFull = true;
        int minGroupSize = groups.stream().mapToInt(g -> g.godfathers.size() + g.godsons.size()).min().orElse(Integer.MAX_VALUE);
        for (Bus bus : buses) {
            if (bus.getPlacesLeft() >= minGroupSize) {
                busFull = false;
                break;
            }
        }
        if (busFull) {
            return groups.stream().mapToInt(g -> g.godfathers.size() + g.godsons.size()).sum();
        }
        int min = Integer.MAX_VALUE;
        Bus[] minCopyBuses = null;
        FinalGFGroup nextGroup = groups.get(0);
        groups.remove(nextGroup);
        for (int i = 0; i < buses.length; i++) {
            Bus bus = buses[i];
            if (bus.getPlacesLeft() >= nextGroup.godfathers.size() + nextGroup.godsons.size()) {
                Bus copyBus = new Bus(bus.places);
                copyBus.groups.addAll(bus.groups);
                copyBus.groups.add(nextGroup);

                Bus[] copyBuses = new Bus[buses.length];
                for (int j = 0; j < buses.length; j++) {
                    copyBuses[j] = new Bus(buses[j].places);
                    copyBuses[j].groups.addAll(buses[j].groups);
                }
                copyBuses[i] = copyBus;
                int result = dispatchBuses(copyBuses, new ArrayList<>(groups));
                if (result < min) {
                    min = result;
                    minCopyBuses = copyBuses;
                    if (min == 0) {
                        break;
                    }
                }
            }
        }

        if (minCopyBuses != null) {
            for (int i = 0; i < buses.length; i++) {
                buses[i].groups.clear();
                buses[i].groups.addAll(minCopyBuses[i].groups);
            }
        }

        return min;
    }

    private static class Student {
        private final String name;
        private final String surname;
        private final String classe;
        private final String rank;
        private final String sex;
        private final String from;
        private boolean taken = false;

        public Student(String name, String surname, String classe, String rank, String sex, String from) {
            this.name = name;
            this.surname = surname;
            this.classe = classe;
            this.rank = rank;
            this.sex = sex;
            this.from = from;
        }

        public Student(String name, String surname, String classe) {
            this(name, surname, classe, "", "", "");
        }

        @Override
        public String toString() {
            return "Student(" + surname + " " + name + ") from " + from;
        }
    }

    private static class FinalGFGroup {
        private boolean inBus = false;
        private final List<Student> godfathers = new ArrayList<>();
        private final List<Student> godsons = new ArrayList<>();

        public String toLatexTable() {
            StringBuilder builder = new StringBuilder();

            builder.append("\\makecell[l]{");
            for (int i = 0; i < godfathers.size(); i++) {
                Student godfather = godfathers.get(i);
                builder.append(godfather.name).append(" ").append(godfather.surname).append(" (").append(godfather.classe).append(")");
                if (i == godfathers.size() - 1) {
                    builder.append("}");
                } else {
                    builder.append(" \\\\ \n");
                }
            }
            builder.append("& \n\\makecell[l]{");
            for (int i = 0; i < godsons.size(); i++) {
                Student godson = godsons.get(i);
                builder.append(godson.name).append(" ").append(godson.surname).append(" (").append(godson.classe).append(")");
                if (i == godsons.size() - 1) {
                    builder.append("}");
                } else {
                    builder.append(" \\\\ \n");
                }
            }
            builder.append("\\\\\n\\hline\n");
            return builder.toString();
        }
    }

    private static class Bus {
        private final List<FinalGFGroup> groups = new ArrayList<>();
        private final int places;

        private Bus(int places) {
            this.places = places;
        }

        public int size() {
            return groups.stream().mapToInt(g -> g.godfathers.size() + g.godsons.size()).sum();
        }

        public int getPlacesLeft() {
            return places - size();
        }
    }

}
