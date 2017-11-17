package it.gruppoinfor.home2work.custom;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


public class AvatarViewBehavior extends CoordinatorLayout.Behavior<AvatarView> {
    /**
     * Default constructor for instantiating a FancyBehavior in code.
     */
    public AvatarViewBehavior() {
    }

    /**
     * Default constructor for inflating a FancyBehavior from layout.
     *
     * @param context The {@link Context}.
     * @param attrs The {@link AttributeSet}.
     */
    public AvatarViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Extract any custom attributes out
        // preferably prefixed with behavior_ to denote they
        // belong to a behavior
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, AvatarView child, View dependency) {
        return dependency instanceof LinearLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, AvatarView child, View dependency) {
        Log.i("BOH", dependency.getHeight() + " " + dependency.getWidth());
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
