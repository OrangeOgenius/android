package com.orange.tpms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<Item, Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {

    protected Context mContext;//上下文
    private List<Item> mRecyclerItems;//数据源

    public BaseRecyclerAdapter(Context context) {
        this(context, null);
    }

    public BaseRecyclerAdapter(Context context, List<Item> items) {
        this.mContext = context;
        this.mRecyclerItems = items;
    }

    /**
     * 创建相应视图
     *
     * @return 视图相关类
     */
    protected abstract Holder onCreateView(ViewGroup parent, int viewType);

    /***
     * 加载item布局
     * @param layoutRes 布局item
     * @param parent 父容器
     * @return item布局
     */
    protected View inflate(int layoutRes, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutRes, parent, false);
    }


    /**
     * 绑定数据
     *
     * @param holder 视图相关类
     * @param index  下标索引
     */
    protected abstract void onBindView(Holder holder, int index);

    @Override
    final public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateView(parent, viewType);
    }

    @Override
    final public void onBindViewHolder(Holder holder, int position) {
        onBindView(holder, position);
    }

    @Override
    final public void onBindViewHolder(Holder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    /**
     * 填充资源
     *
     * @param resource 布局的layout文件
     * @return item布局
     */
    public View getInflateView(int resource) {
        return LayoutInflater.from(mContext).inflate(resource, null);
    }

    /**
     * 获取数据集合
     */
    public List<Item> getItems() {
        if (mRecyclerItems == null) {
            mRecyclerItems = new ArrayList<>();
        }
        return mRecyclerItems;
    }

    /**
     * 设置数据集合
     *
     * @param mRecyclerItems 数据集合
     */
    public void setItems(List<Item> mRecyclerItems) {
        this.mRecyclerItems = mRecyclerItems;
    }

    /**
     * 获取莫一个位置的数据
     *
     * @param index 位置
     */
    public Item getItem(int index) {
        if (index >= 0 && index < getItemCount()) {
            return getItems().get(index);
        }
        return null;
    }

    /**
     * 添加数据
     *
     * @param item 数据
     */
    public void addItem(Item item) {
        if (mRecyclerItems == null) {
            mRecyclerItems = new ArrayList<>();
        }
        this.mRecyclerItems.add(item);
    }

    /**
     * 添加数据
     *
     * @param index 添加位置
     * @param item  数据
     */
    public void setItem(int index, Item item) {
        if (mRecyclerItems == null) {
            mRecyclerItems = new ArrayList<>();
        }
        this.mRecyclerItems.set(index, item);
    }

    /**
     * 添加数据
     *
     * @param index 添加位置
     * @param item  数据
     */
    public void addItem(int index, Item item) {
        if (mRecyclerItems == null) {
            mRecyclerItems = new ArrayList<>();
        }
        this.mRecyclerItems.add(index, item);
    }

    /**
     * 添加数据集合
     *
     * @param items 数据集合
     */
    public void addItems(List<Item> items) {
        if (mRecyclerItems == null) {
            mRecyclerItems = new ArrayList<>();
        }
        this.mRecyclerItems.addAll(items);
    }

    /**
     * 添加数据集合
     *
     * @param index 添加位置
     * @param items 数据集合
     */
    public void addItems(int index, List<Item> items) {
        if (mRecyclerItems == null) {
            mRecyclerItems = new ArrayList<>();
        }
        this.mRecyclerItems.addAll(index, items);
    }

    /**
     * 清楚item数据
     */
    public void clean() {
        mRecyclerItems = new ArrayList<>();
    }
}
