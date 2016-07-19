package com.zopim.sample.chatapi.chat.log;

import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.sample.chatapi.chat.log.items.ViewHolderWrapper;

import java.util.List;
import java.util.Map;

public interface ChatLogMvp {

    interface Presenter {

        void updateChatLog(Map<String, RowItem> chatItemMap);

        int getItemCount();

        ViewHolderWrapper getViewHolderWrapperForPos(int position);
    }

    interface View {

        void notifyInserted(List<Integer> index);

        void notifyUpdated(List<Integer> index);

        void refreshWholeList();

        <E extends ChatLogMvp.Presenter> void setPresenter(E presenter);

        void scrollToLastMessage();
    }

    interface Model {

        ViewHolderWrapper getViewHolderWrapperForPos(int position);

        int getItemCount();

        ChatLogUpdateResult updateChatLog(Map<String, RowItem> chatItemMap);
    }

}
