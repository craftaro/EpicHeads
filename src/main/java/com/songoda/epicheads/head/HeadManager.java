package com.songoda.epicheads.head;

import java.util.*;
import java.util.stream.Collectors;

public class HeadManager {

    private static final Set<Head> registeredHeads = new HashSet<>();
    private static final List<Tag> registeredTags = new ArrayList<>();

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

    public Head getHead(String name) {
        return getHeads().stream().filter(head -> head.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Head> getHeadsByQuery(String query) {
        List<Head> result = getHeads().stream().filter(head -> head.getName().contains(query)).collect(Collectors.toList());

        if (result.isEmpty()) {
            for (Tag tag : registeredTags) {
                if (!tag.getName().equalsIgnoreCase(query)) continue;
                return getHeads().stream().filter(head -> head.getTag() == tag).collect(Collectors.toList());

            }
        }
        return result;
    }

    public List<Head> getHeadsByTag(Tag tag) {
        return getHeads().stream().filter(head -> head.getTag() == tag).collect(Collectors.toList());
    }

    public List<Head> getHeads() {
        return Collections.unmodifiableList(registeredHeads.stream()
                .sorted(Comparator.comparing(Head::getName))
                .sorted(Comparator.comparingInt(head -> (head.getStaffPicked() == 1 ? 0 : 1)))
                .collect(Collectors.toList()));
    }

    public Tag addTag(Tag tag) {
        registeredTags.add(tag);
        return tag;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(registeredTags);
    }
}
