package com.fooddelivery.customer.domain.rule.hatefulname;

import com.fooddelivery.customer.common.datastructures.TrieMap;

import java.util.Optional;


public class HatefulNameRule {
    private static final TrieMap forbiddenNames = new TrieMap();

    /**
     * TODO: Temporary solution.
     *       This will be moved to the DB.
     */
    static {
        forbiddenNames.add("fuck");
        forbiddenNames.add("kill");
        forbiddenNames.add("dick");
    }

    public static boolean isValid(String name) {
        for(int i = 0; i < name.length(); i++) {
            TrieMap.Node currNode = forbiddenNames.getRoot();
            int currIdx = i;

            while (currNode != null && currIdx < name.length()) {
                final Optional<TrieMap.Node> child = forbiddenNames.get(currNode, name.charAt(currIdx));

                if(child.isEmpty()) {
                    break;
                }

                currNode = child.get();

                if(currNode.isEndOfWorld()) {
                    return false;
                }

                currIdx++;
            }
        }

        return true;
    }
}
