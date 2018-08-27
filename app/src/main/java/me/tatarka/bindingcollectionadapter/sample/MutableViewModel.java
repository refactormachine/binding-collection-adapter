package me.tatarka.bindingcollectionadapter.sample;

import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import me.tatarka.bindingcollectionadapter2.BindingListViewAdapter;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

public class MutableViewModel extends ViewModel implements Listeners{

    public final ObservableList<Item> items = new ObservableArrayList<>();
    private boolean checkable;

    /**
     * Items merged with a header on top and footer on bottom.
     */
    public final MergeObservableList<Object> headerFooterItems = new MergeObservableList<>()
            .insertItem("Header")
            .insertList(items)
            .insertItem("Footer");

    /**
     * Custom adapter that logs calls.
     */
    public final LoggingRecyclerViewAdapter<Object> adapter = new LoggingRecyclerViewAdapter<>();

    public MutableViewModel() {
        for (int i = 0; i < 3; i++) {
            items.add(new Item(i));
        }
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
        for (Item item : items) {
            item.checkable = checkable;
        }
    }

    /**
     * Binds a homogeneous list of items to a layout.
     */
    public final ItemBinding<Item> singleItem = ItemBinding.of(BR.item, R.layout.item);

    public final ItemBinding<Item> pageItem = ItemBinding.of(BR.item, R.layout.item_page);

    /**
     * Binds multiple items types to different layouts based on class. This could have also be
     * written manually as
     * <pre>{@code
     * public final OnItemBind<Object> multipleItems = new OnItemBind<Object>() {
     *     @Override
     *     public void onItemBind(ItemBinding itemBinding, int position, Object item) {
     *         if (String.class.equals(item.getClass())) {
     *             itemBinding.set(BR.item, R.layout.item_header_footer);
     *         } else if (Item.class.equals(item.getClass())) {
     *             itemBinding.set(BR.item, R.layout.item);
     *         }
     *     }
     * };
     * }</pre>
     */
    public final OnItemBindClass<Object> multipleItems = new OnItemBindClass<>()
            .map(String.class, BR.item, R.layout.item_header_footer)
            .map(Item.class, BR.item, R.layout.item);

    /**
     * Define stable item ids. These are just based on position because the items happen to not
     * every move around.
     */
    public final BindingListViewAdapter.ItemIds<Object> itemIds = new BindingListViewAdapter.ItemIds<Object>() {
        @Override
        public long getItemId(int position, Object item) {
            return position;
        }
    };

    /**
     * Define page titles for a ViewPager
     */
    public final BindingViewPagerAdapter.PageTitles<Item> pageTitles = new BindingViewPagerAdapter.PageTitles<Item>() {
        @Override
        public CharSequence getPageTitle(int position, Item item) {
            return "Item " + (item.getIndex() + 1);
        }
    };

    /**
     * Custom view holders for RecyclerView
     */
    public final BindingRecyclerViewAdapter.ViewHolderFactory viewHolder = new BindingRecyclerViewAdapter.ViewHolderFactory() {
        @Override
        public RecyclerView.ViewHolder createViewHolder(ViewDataBinding binding) {
            return new MyAwesomeViewHolder(binding.getRoot());
        }
    };

    private static class MyAwesomeViewHolder extends RecyclerView.ViewHolder {
        public MyAwesomeViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onAddItem() {
        Item item = new Item(items.size());
        item.checkable = checkable;
        items.add(item);
    }

    @Override
    public void onRemoveItem() {
        if (items.size() > 1) {
            items.remove(items.size() - 1);
        }
    }
}
