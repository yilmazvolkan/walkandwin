package com.boun.volkanyilmaz.walkandwin.swipe;

import android.content.Context;
import android.widget.TextView;

import com.boun.volkanyilmaz.walkandwin.R;
import com.boun.volkanyilmaz.walkandwin.models.Advertisement;
import com.boun.volkanyilmaz.walkandwin.models.UserData;
import com.mindorks.placeholderview.Animation;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Animate;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

/**
 * Created by volkanyilmaz on 26/02/18.
 */

@Animate(Animation.ENTER_LEFT_DESC)
@NonReusable
@Layout(R.layout.start_slide_card)
public class StarCard {

  @View(R.id.comAdv)
  private TextView mes;

  @View(R.id.comName)
  private TextView name;

  private Advertisement mAdv;
  private Context mContext;
  private SwipePlaceHolderView mSwipeView;
  private UserData mUserData;

  public StarCard(Context context, SwipePlaceHolderView swipeView, UserData userData) {

    mContext = context;
    mUserData = userData;
    mSwipeView = swipeView;
  }

  @Resolve
  private void onResolved(){
    name.setText("TEBRIKLER!");
    mes.setText("BU YOLCULUGU BASARIYLA TAMAMLADINIZ VE BIZDEN " + mUserData.getScore() + " KADAR PUAN KAZANDINIZ. PUANLARINIZI ISTEDIGINIZ YERDE HARCAYABILIRSINIZ.");
  }
}