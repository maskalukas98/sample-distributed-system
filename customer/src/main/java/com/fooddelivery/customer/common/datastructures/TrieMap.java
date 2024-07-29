package com.fooddelivery.customer.common.datastructures;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TrieMap {
    public final static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean endOfWorld = false;
        String value = null;

        public boolean isEndOfWorld() {
            return endOfWorld;
        }
    }

    private final Node root = new Node();

    public void add(String word) {
        Node current = root;

        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new Node());
            current = current.children.get(ch);
        }

        current.endOfWorld = true;
        current.value = word;
    }

    public Optional<Node> get(Node node, Character ch) {
        if(node.children.containsKey(ch)) {
            return Optional.of(node.children.get(ch));
        }

        return Optional.empty();
    }

    public Node getRoot() {
        return root;
    }
}
