package com.zendesk.chat_v2.sample;

import static zendesk.messaging.DialogContent.Config.TRANSCRIPT_PROMPT;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import zendesk.chat.Chat;
import zendesk.chat.ChatEngine;
import zendesk.chat.ChatProvider;
import zendesk.chat.ChatRating;
import zendesk.chat.ChatState;
import zendesk.messaging.DialogContent;
import zendesk.messaging.Engine;
import zendesk.messaging.Event;
import zendesk.messaging.Event.DialogItemClicked;
import zendesk.messaging.Event.MenuItemClicked;
import zendesk.messaging.MenuItem;
import zendesk.messaging.MessagingApi;
import zendesk.messaging.ObservableEngine;
import zendesk.messaging.Update;
import zendesk.messaging.Update.ApplyMenuItems;
import zendesk.messaging.Update.ShowDialog;

/**
 * A proof of concept engine to illustrate how to inject custom logic between the UI and the driving
 * engine.
 */
@SuppressLint("RestrictedApi")
public class ChatInterceptorEngine extends ObservableEngine {

    public static ChatInterceptorEngine engine(Context context) {
        return new ChatInterceptorEngine(ChatEngine.engine(), Chat.INSTANCE.providers().chatProvider(), context);
    }

    private static final int MENU_ITEM_RATE_CHAT = 1000001;
    private final Engine engine;
    private final ChatProvider chatProvider;
    private final DialogContent rateChatDialog;
    private boolean isRateDialogShown;

    ChatInterceptorEngine(Engine engine, ChatProvider chatProvider, Context context) {
        this.engine = engine;
        this.chatProvider = chatProvider;
        // NOTE: this dialog shows a title, message and 2 buttons
        // with hardcoded "YES" and "NO" labels that currently cannot be changed.
        this.rateChatDialog = new DialogContent.Builder(TRANSCRIPT_PROMPT)
                .withTitle(context.getString(R.string.chat_menu_item_rate))
                .withMessage(context.getString(R.string.rate_chat_dialog_message))
                .build();
        this.isRateDialogShown = false;
        this.engine.registerObserver(this::interceptUpdate);
    }

    @Override
    public void start(@NonNull MessagingApi messagingApi) {
        engine.start(messagingApi);
    }

    @Override
    public void stop() {
        engine.stop();
    }

    @NonNull
    @Override
    public String getId() {
        return engine.getId();
    }

    @Nullable
    @Override
    public TransferOptionDescription getTransferOptionDescription() {
        return engine.getTransferOptionDescription();
    }

    @Override
    public void isConversationOngoing(ConversationOnGoingCallback conversationOnGoingCallback) {
        engine.isConversationOngoing(conversationOnGoingCallback);
    }

    @Override
    public void onEvent(@NonNull Event event) {
        if (event instanceof MenuItemClicked) {
            MenuItemClicked menuItemClickedEvent = (MenuItemClicked) event;

            if (menuItemClickedEvent.getMenuItemId() == MENU_ITEM_RATE_CHAT) {
                isRateDialogShown = true;
                notifyObservers(new ShowDialog(rateChatDialog));
                return;
            }
        }
        if (event instanceof DialogItemClicked && isRateDialogShown) {
            isRateDialogShown = false;
            Event.DialogItemClicked dialogItemClickedEvent = (Event.DialogItemClicked) event;
            chatProvider.sendChatRating(dialogItemClickedEvent.isPositive() ? ChatRating.GOOD : ChatRating.BAD, null);
            return;
        }
        engine.onEvent(event);
    }

    /**
     * Intercepts the {@link Update} and inserts a menu item to rate the chat given that: - there's an active
     * conversation between the visitor and an agent - the current chat has not yet been rated by the visitor
     *
     * @param update an update that might be overridden to insert an additional menu item
     */
    private void interceptUpdate(Update update) {
        boolean showRateChatMenuItem = isChatting() && !isChatAlreadyRated();
        if (update instanceof ApplyMenuItems && showRateChatMenuItem) {
            ApplyMenuItems applyMenuItemsUpdate = (ApplyMenuItems) update;
            List<MenuItem> menuItems = applyMenuItemsUpdate.getMenuItems();
            menuItems.add(0, new MenuItem(MENU_ITEM_RATE_CHAT, R.string.chat_menu_item_rate));
            notifyObservers(new ApplyMenuItems(menuItems));
            return;
        }
        notifyObservers(update);
    }

    /**
     * Checks whether there's an active conversation between the visitor and an agent.
     *
     * @return {@code true} if there is an active conversation, {@code false} otherwise.
     */
    private boolean isChatting() {
        final ChatState chatState = chatProvider.getChatState();
        if (chatState == null) {
            return false;
        }
        return chatState.isChatting();
    }

    /**
     * Checks whether current chat has already been rated by the visitor.
     *
     * @return {@code true} if the visitor already rated the chat, {@code false} otherwise.
     */
    private boolean isChatAlreadyRated() {
        final ChatState chatState = chatProvider.getChatState();
        if (chatState == null) {
            return false;
        }
        return chatState.getChatRating() != null;
    }
}
