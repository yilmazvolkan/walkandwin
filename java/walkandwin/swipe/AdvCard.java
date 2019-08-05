package com.boun.volkanyilmaz.walkandwin.swipe;

import android.content.Context;
import android.widget.TextView;

import com.boun.volkanyilmaz.walkandwin.R;
import com.boun.volkanyilmaz.walkandwin.models.Advertisement;
import com.mindorks.placeholderview.Animation;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Animate;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

/**
 * Created by volkanyilmaz on 25/02/18.
 */
@Animate(Animation.ENTER_LEFT_DESC)
@NonReusable
@Layout(R.layout.slide_card_view)
public class AdvCard {

  @View(R.id.comAdv)
  private TextView mes;

  @View(R.id.comName)
  private TextView name;

  private Advertisement mAdv;
  private Context mContext;
  private SwipePlaceHolderView mSwipeView;

  public AdvCard(Context context, SwipePlaceHolderView swipeView, Advertisement adv) {

    mContext = context;
    mAdv = adv;
    mSwipeView = swipeView;
  }

  @Resolve
  private void onResolved(){
    mes.setText(mAdv.getAdv().toString());
    name.setText(mAdv.getTitle().toString());
  }
}
