package com.zopim.sample.chatapi.chat;


import android.support.v4.app.FragmentActivity;

import com.zendesk.belvedere.BelvedereResult;
import com.zopim.android.sdk.model.Connection;
import com.zopim.android.sdk.model.items.RowItem;

import java.io.File;
import java.util.List;
import java.util.Map;

interface ChatMvp {

    interface Model {

        void sendMessage(String message);

        void sendAttachment(File file);

        void registerChatListener(ChatListener chatListener);

        void unregisterChatListener();

        void clearChatIfEnded();

        interface ChatListener {

            void onUpdateChatLog(Map<String, RowItem> chatItemMap);

            void onUpdateConnection(Connection connection);

            void onTimeout();
        }
    }

    interface View {

        void initChatUi(FragmentActivity activity);

        <E extends Presenter> void setPresenter(E presenter);

        void updateChatLog(Map<String, RowItem> chatItemMap);

        void setInputEnabled(boolean enabled);

        void showLoading(boolean loading);

        void connectionChanged(boolean connected);

        void timeout();
    }

    interface Presenter {

        void install(FragmentActivity activity);

        void sendMessage(String message);

        void sendFile(List<BelvedereResult> belvedereResult);

        void onDestroy();

        void chatDismissed();
    }
}
