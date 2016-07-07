package com.zopim.sample.chatapi.chat.log;

import java.util.Collections;
import java.util.List;

class ChatLogUpdateResult {

    static ChatLogUpdateResult create(List<Integer> updatedIndex, final List<Integer> insertedIndex) {
        return new ChatLogUpdateResult(updatedIndex, insertedIndex, false);
    }

    static ChatLogUpdateResult create() {
        return new ChatLogUpdateResult(Collections.<Integer>emptyList(), Collections.<Integer>emptyList(), true);
    }

    private final List<Integer> updatedIndex;
    private final List<Integer> insertedIndex;
    private final boolean unableToDoIncrementalUpdate;

    private ChatLogUpdateResult(final List<Integer> updatedIndex, final List<Integer> insertedIndex, final boolean unableToDoIncrementalUpdate) {
        this.updatedIndex = updatedIndex;
        this.insertedIndex = insertedIndex;
        this.unableToDoIncrementalUpdate = unableToDoIncrementalUpdate;
    }

    List<Integer> getUpdatedIndex() {
        return updatedIndex;
    }

    List<Integer> getInsertedIndex() {
        return insertedIndex;
    }

    boolean isUnableToDoIncrementalUpdate() {
        return unableToDoIncrementalUpdate;
    }
}
