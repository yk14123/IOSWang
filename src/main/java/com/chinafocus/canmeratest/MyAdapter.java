package com.chinafocus.canmeratest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    // 缓存AppCompatImageView，以便选中bingo位后，让其他未选中的View执行飞出去动画
    private List<AppCompatImageView> imageViewList;
    // 整个View的数量
    private int items;
    // 表示该位置是随机中奖位！
    private int bingo;

    private InterstitialAd mInterstitialAd;

    public MyAdapter(int spanCount, InterstitialAd interstitialAd) {
        this.items = spanCount;
        imageViewList = new ArrayList<>();
        randomSize(spanCount);
        mInterstitialAd = interstitialAd;
    }

    /**
     * 在 0 到 (items-1)之间随机生成bingo值
     *
     * @param spanCount
     */
    private void randomSize(int spanCount) {
        int min = 0;
        bingo = (int) (Math.random() * (spanCount - min) + min);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(rootView);
        final AppCompatImageView imageView = myViewHolder.imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (myViewHolder.getAdapterPosition() == bingo) {
                    ((AppCompatImageView) v).setImageResource(R.drawable.cry);
                    imageViewList.remove(v);
                    if (imageViewList.size() == 0) {
                        animTranslationEnd(v);
                        return;
                    }
                    for (AppCompatImageView temp : imageViewList) {
                        animTranslationY(temp);
                    }
                    imageViewList.clear();
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animTranslationEnd(v);
                        }
                    }, 500);
                } else {
                    animTranslationY(v);
                    imageViewList.remove(v);
                }
            }
        });

        imageViewList.add(imageView);

        fixRecyclerViewAutoHeight(myViewHolder, parent);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items;
    }

    /**
     * 让整体的RecyclerView，呈现一个以宽为边长的正方形
     * 因为RecyclerView的高度是wrap_content，所以每个条目的高度 = RecyclerView的宽 / spanCount
     *
     * @param myViewHolder
     * @param parent
     */
    private void fixRecyclerViewAutoHeight(RecyclerView.ViewHolder myViewHolder, ViewGroup parent) {
        ViewGroup.LayoutParams layoutParams = myViewHolder.itemView.getLayoutParams();
        GridLayoutManager layoutManager = (GridLayoutManager) ((RecyclerView) parent).getLayoutManager();
        // height = 1080 / spanCount
        layoutParams.height = layoutManager.getWidth() / layoutManager.getSpanCount();
        myViewHolder.itemView.setLayoutParams(layoutParams);
    }

    private View bingoView;

    public void clear() {
        if (bingoView != null) {
            ViewGroup parent = (ViewGroup) bingoView.getParent();
            parent.removeView(bingoView);
            bingoView = null;
        }
    }

    /**
     * RecycleView中任意一个View，平移+缩放到RecycleView的中心
     *
     * @param v
     */
    private void animTranslationEnd(final View v) {
        bingoView = v;
        // Our view is added to the parent of its parent, the most top-level layout
        final ViewGroup container = (ViewGroup) v.getParent().getParent();
        if (container != null) {
            container.getOverlay().add(v);

            int[] recyclerPos = new int[2];
            container.getLocationOnScreen(recyclerPos);
            int centerWidth = container.getWidth() / 2;
            int centerHeight = container.getWidth() / 2;

            int[] vPos = new int[2];
            v.getLocationOnScreen(vPos);
            int vWidth = v.getWidth();
            int vHeight = v.getHeight();
            int vCenterX = vWidth / 2 + vPos[0];
            int vCenterY = vHeight / 2 + vPos[1];

            int offsetX = centerWidth - vCenterX;
            int offsetY = centerHeight - vCenterY;

            float scale = centerWidth * 2.f / vWidth;

            ObjectAnimator animY = ObjectAnimator.ofFloat(v, "translationY", offsetY);
            ObjectAnimator animX = ObjectAnimator.ofFloat(v, "translationX", offsetX);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1, scale);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1, scale);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(1000);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (getChance(20)) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }
                    }
                }
            });
            set.playTogether(animX, animY, scaleX, scaleY);
            set.start();
        }
    }

    public boolean getChance(int percentage) {
        Random random = new Random();
        int i = random.nextInt(99);
        Log.d("TAG", " i >> " + i);
        return i >= 0 && i < percentage;
    }

    /**
     * RecycleView中任意一个View，平移向上飞出屏幕
     *
     * @param v
     */
    private void animTranslationY(final View v) {
        // Our view is added to the parent of its parent, the most top-level layout
        final ViewGroup container = (ViewGroup) v.getParent().getParent().getParent();
        container.getOverlay().add(v);

        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "translationY", -container.getHeight());

        /*
         * Views needs to be removed after animation ending
         * When we have added the view to the ViewOverlay,
         * it was removed from its original parent.
         */
        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                container.getOverlay().remove(v);
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
                container.getOverlay().remove(v);
            }
        });

        anim.setDuration(1000);

        anim.start();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public final AppCompatImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item);

        }
    }
}
