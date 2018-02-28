package org.socratic.android.util;

/**
 * Created by byfieldj on 9/20/17.
 * <p>
 * This class downloads images using Glide and replaces all <img> tags found in HTML text
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import org.socratic.android.R;

import java.util.HashSet;
import java.util.Set;

public final class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {

    private final Context mContext;

    private final TextView mTextView;

    private final Set<ImageGetterViewTarget> mTargets;


    public static GlideImageGetter get(View view) {
        return (GlideImageGetter) view.getTag(R.id.answer_text);
    }

    public void clear() {
        GlideImageGetter prev = get(mTextView);
        if (prev == null) return;

        for (ImageGetterViewTarget target : prev.mTargets) {
            Glide.clear(target);
        }
    }

    public GlideImageGetter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;

        clear();
        mTargets = new HashSet<>();
        mTextView.setTag(R.id.answer_text, this);
    }

    @Override
    public Drawable getDrawable(String url) {
        final UrlDrawable urlDrawable = new UrlDrawable();


        System.out.println("Downloading from: " + url);
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ImageGetterViewTarget(mTextView, urlDrawable));


        return urlDrawable;

    }

    @Override
    public void invalidateDrawable(Drawable who) {
        mTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {

    }

    private class ImageGetterViewTarget extends ViewTarget<TextView, GlideDrawable> {

        private final UrlDrawable mDrawable;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable) {
            super(view);
            mTargets.add(this);
            this.mDrawable = drawable;
        }

        // Once our drawable is ready, load the resource into our image target
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            Rect rect;
            if (resource.getIntrinsicWidth() > 100) {
                float width;
                float height;
                System.out.println("Image width is " + resource.getIntrinsicWidth());
                System.out.println("View width is " + view.getWidth());
                if (resource.getIntrinsicWidth() >= getView().getWidth()) {

                    // If the width of the image target is wider than the TextView width
                    // Scale the image down to be at-most as wide as the TextView
                    float downScale = (float) resource.getIntrinsicWidth() / getView().getWidth();
                    width = (float) resource.getIntrinsicWidth() / (float) downScale;
                    height = (float) resource.getIntrinsicHeight() / (float) downScale;

                } else {
                    float multiplier = (float) getView().getWidth() / resource.getIntrinsicWidth();
                    width = (float) resource.getIntrinsicWidth() * (float) multiplier;
                    height = (float) resource.getIntrinsicHeight() * (float) multiplier;
                }
                System.out.println("New Image width is " + width);


                rect = new Rect(0, 50, Math.round(width), Math.round(height));
            } else {
                rect = new Rect(0, 50, resource.getIntrinsicWidth() * 2, resource.getIntrinsicHeight() * 2);
            }
            resource.setBounds(rect);

            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);


            // If the image target is a GIF or another time of animating image
            // Loop through its frames and animate the result
            if (resource.isAnimated()) {
                mDrawable.setCallback(get(getView()));
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                resource.start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }

        private Request request;

        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public void setRequest(Request request) {
            this.request = request;
        }
    }


    public class UrlDrawable extends Drawable {
        public GlideDrawable drawable;

        public UrlDrawable() {
            super();
        }

        public void setDrawable(GlideDrawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public void setAlpha(int alpha) {
            if (drawable != null) {
                drawable.setAlpha(alpha);
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            if (drawable != null) {
                drawable.setColorFilter(cf);
            }
        }

        @Override
        public int getOpacity() {
            if (drawable != null) {
                return drawable.getOpacity();
            }
            return PixelFormat.OPAQUE;
        }

        @Override
        public void draw(Canvas canvas) {
        // override the draw to facilitate refresh function later
            if (drawable != null) {
                Paint p = new Paint();
                p.setColor(Color.GREEN);
                canvas.drawRect(drawable.getBounds(), p);
                drawable.draw(canvas);

                if (!drawable.isRunning()) {
                    drawable.start();
                }
            }
        }
    }
}