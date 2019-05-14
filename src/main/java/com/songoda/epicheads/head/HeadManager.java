package com.songoda.epicheads.head;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeadManager {

    private static final Set<Head> registeredHeads = new HashSet<>();
    private static final List<Head> localRegisteredHeads = new ArrayList<>();
    private static final List<Category> registeredCategories = new ArrayList<>();

    public Head addHead(Head head) {
        registeredHeads.add(head);
        return head;
    }

    public void addHeads(Head... heads) {
        registeredHeads.addAll(Arrays.asList(heads));
    }

    public void addHeads(Collection<Head> heads) {
        registeredHeads.addAll(heads);
    }

    public void addLocalHeads(Head... heads) {
        localRegisteredHeads.addAll(Arrays.asList(heads));
    }

    public void addLocalHead(Head head) {
        localRegisteredHeads.add(head);
    }

    public void addLocalHeads(Collection<Head> heads) {
        localRegisteredHeads.addAll(heads);
    }

    public Head getHead(String name) {
        return getHeads().stream().filter(head -> head.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Head> getHeadsByQuery(String query) {
        List<Head> result = getHeads().stream().filter(head -> head.getName().contains(query)).collect(Collectors.toList());

        if (result.isEmpty()) {
            for (Category category : registeredCategories) {
                if (!category.getName().equalsIgnoreCase(query)) continue;
                return getHeads().stream().filter(head -> head.getCategory() == category).collect(Collectors.toList());

            }
        }
        return result;
    }

    public List<Head> getHeadsByCategory(Category category) {
        return getHeads().stream().filter(head -> head.getCategory() == category).collect(Collectors.toList());
    }

    public List<Head> getHeads() {
        return Collections.unmodifiableList(Stream.concat(registeredHeads.stream(), localRegisteredHeads.stream())
                .sorted(Comparator.comparing(Head::getName))
                .sorted(Comparator.comparingInt(head -> (head.getStaffPicked() == 1 ? 0 : 1)))
                .collect(Collectors.toList()));
    }

    public Integer getNextLocalId() {
        if (localRegisteredHeads.isEmpty()) return 1;
        return localRegisteredHeads.get(localRegisteredHeads.size() - 1).getId() + 1;
    }

    public List<Head> getLocalHeads() {
        return Collections.unmodifiableList(localRegisteredHeads);
    }

    public List<Head> getGlobalHeads() {
        return new ArrayList<>(registeredHeads);
    }

    public List<Head> getLatestPack() {
        List<Head> heads = getHeads().stream().sorted(Comparator.comparingInt(Head::getId)).filter(head -> head.getPack() != null).collect(Collectors.toList());

        Collections.reverse(heads);

        if (heads.isEmpty()) return new ArrayList<>();

        String latestPack = heads.get(0).getPack();

        heads = heads.stream().filter(head -> head.getPack().equals(latestPack)).collect(Collectors.toList());

        return heads;
    }

    public void removeLocalHead(Head head) {
        localRegisteredHeads.remove(head);
    }

    public Category addCategory(Category category) {
        registeredCategories.add(category);
        return category;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(registeredCategories);
    }

    public void clear() {
        registeredHeads.clear();
        localRegisteredHeads.clear();
        registeredCategories.clear();
    }
}
