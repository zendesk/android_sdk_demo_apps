package com.zopim.sample.chatapi.chat.log;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopim.sample.chatapi.R;
import com.zopim.sample.chatapi.chat.log.items.ItemType;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} used to render the chat log.
 */
public class ChatLogView extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ChatLogMvp.View {

    private final RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ChatLogMvp.Presenter presenter;

    public ChatLogView(RecyclerView.LayoutManager recyclerView) {
        this.recyclerViewLayoutManager = recyclerView;
        setHasStableIds(true);
    }

    @Override
    public <E extends ChatLogMvp.Presenter> void setPresenter(final E presenter) {
        this.presenter = presenter;
    }

    @Override
    public int getItemViewType(final int position) {
        return presenter
                .getViewHolderWrapperForPos(position)
                .getItemType()
                .getViewType();
    }

    @Override
    public long getItemId(final int position) {
        return presenter.getViewHolderWrapperForPos(position).getId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final ItemType itemType = ItemType.forViewType(viewType);

        if (itemType != null) {
            final LayoutInflater li = LayoutInflater.from(parent.getContext());

            final View holderView;
            if (itemType.isAgent()) {
                holderView = li.inflate(R.layout.chat_log_agent_holder, parent, false);
            } else {
                holderView = li.inflate(R.layout.chat_log_visitor_holder, parent, false);
            }

            final ViewGroup holderContainer = (ViewGroup) holderView.findViewById(R.id.chat_log_holder_placeholder);
            li.inflate(itemType.getLayout(), holderContainer, true);

            return new RecyclerView.ViewHolder(holderView) { };
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        presenter.getViewHolderWrapperForPos(position).bind(holder);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    @Override
    public void scrollToLastMessage() {
        recyclerViewLayoutManager.scrollToPosition(getItemCount() - 1);
    }

    @Override
    public void refreshWholeList() {
        notifyDataSetChanged();
    }
}
