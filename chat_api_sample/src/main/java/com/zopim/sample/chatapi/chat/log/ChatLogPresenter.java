package com.zopim.sample.chatapi.chat.log;


import com.zendesk.util.CollectionUtils;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.sample.chatapi.chat.log.items.ViewHolderWrapper;

import java.util.Map;

public class ChatLogPresenter implements ChatLogMvp.Presenter {

    private final ChatLogMvp.View view;
    private final ChatLogMvp.Model model;

    public ChatLogPresenter(ChatLogMvp.View view, ChatLogMvp.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public ViewHolderWrapper getViewHolderWrapperForPos(int position) {
        return model.getViewHolderWrapperForPos(position);
    }

    @Override
    public int getItemCount() {
        return model.getItemCount();
    }

    @Override
    public void updateChatLog(final Map<String, RowItem> chatItemMap) {

        final ChatLogUpdateResult chatLogUpdateResult = model.updateChatLog(chatItemMap);

        if (chatLogUpdateResult.isUnableToDoIncrementalUpdate()) {
            view.refreshWholeList();
            view.scrollToLastMessage();

        } else {

            if (CollectionUtils.isNotEmpty(chatLogUpdateResult.getInsertedIndex())) {
                view.notifyInserted(chatLogUpdateResult.getInsertedIndex());
                view.scrollToLastMessage();
            }

            if (CollectionUtils.isNotEmpty(chatLogUpdateResult.getUpdatedIndex())) {
                view.notifyUpdated(chatLogUpdateResult.getUpdatedIndex());
            }
        }
    }

}
