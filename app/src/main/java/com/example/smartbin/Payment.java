package com.example.smartbin;

import static android.content.ContentValues.TAG;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Objects;

public class Payment extends AppCompatActivity {
    CardView send;
    TextView send2,text,text2;
    final int UPI_PAYMENT = 0;
    int cost;
    ImageView i1,i2,i3,i4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cost = getIntent().getIntExtra("Cost", 0); // Use "Cost" as the key
        Log.d(TAG, "onCreate: "+cost);
        int a=0; // use like case to open gpay,payth etc;
        send2 = findViewById(R.id.send2);
        send = findViewById(R.id.send);
        i1=findViewById(R.id.img1);
        i2=findViewById(R.id.img2);
        i3=findViewById(R.id.img3);
        i4=findViewById(R.id.img4);
        text=findViewById(R.id.text);
        text.setText(String.valueOf("Your total is \n Rs "+cost));
        text2=findViewById(R.id.amt);
        text2.setText("Rs "+cost);
        RelativeLayout scrollView = findViewById(R.id.scrollable);
        Objects.requireNonNull(getSupportActionBar()).hide();

        CardView card1 = findViewById(R.id.card3);
        CardView card2 = findViewById(R.id.card4);

        scrollView.setOnTouchListener(new OnSwipeTouchListener(this) {
            boolean showingCard1 = true;

            public void onSwipeRight() {
                if (!showingCard1) {
                    showingCard1 = true;
                    animateTransition(card2, card1, false);
                }
            }

            public void onSwipeLeft() {
                if (showingCard1) {
                    showingCard1 = false;
                    animateTransition(card1, card2, true);
                }
            }
        });


        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUpi(1);
            }
        });
        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUpi(2);
            }
        });

        send2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUpi(a);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payUsingUpi(a);
            }
        });
    }
    public void changeCardColor(View view) {
        CardView cardView = findViewById(R.id.send);
        cardView.setCardBackgroundColor(Color.parseColor("#339A38"));
        // Apply a scale animation
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(cardView, "scaleX", 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(cardView, "scaleY", 0.9f);
        scaleDownX.setDuration(200);
        scaleDownY.setDuration(200);

        // Play the scale down animation
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    void payUsingUpi(int a) {
        String amount = "1.00"; // You can dynamically set this amount
        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", "slaydesequeira03@okhdfcbank") // your personal VPA
                .appendQueryParameter("pn", "Slayde Sequeira") // your name
                .appendQueryParameter("am", String.valueOf(cost)) // amount
                .appendQueryParameter("cu", "INR") // currency
                .appendQueryParameter("tn", "For personal use") // transaction note
                .build();
        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        int GOOGLE_PAY_REQUEST_CODE = 123;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        if(a==1)
        {
            Intent paytmIntent = getPackageManager().getLaunchIntentForPackage("net.one97.paytm");
            if (paytmIntent != null) {
                startActivity(paytmIntent);
            } else {
                // Paytm app is not installed, handle this case accordingly
                // For example, redirect the user to download the Paytm app from the Play Store
            }
        }
        if(a==2) {
            startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);          //use if u only wanna show google pay
        }
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(Payment.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        // Handle the payment response here
                    } else {
                        // Handle case when return data is null
                    }
                } else {
                    // Handle case when user simply back without payment
                }
                break;
        }
    }
    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        return true;
                    }
                }
                return false;
            }
        }
    }
    private void animateTransition(View fromView, View toView, boolean isSwipeLeft) {
        // Increase elevation of 'fromView' temporarily during the animation
        fromView.setElevation(12f); // Set the desired elevation value

        // Fading, Rotating, Scaling, and Translating Animation for the "from" view
        fromView.animate()
                .alpha(0.0f)
                .rotation(isSwipeLeft ? -45f : 45f) // Rotate the view
                .scaleX(0.5f) // Scale down the view to 50%
                .scaleY(0.5f)
                .translationX(isSwipeLeft ? -fromView.getWidth() * 2f : fromView.getWidth() * 2f) // Move off the screen
                .setDuration(800) // Adjust this duration for the disappearance speed
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fromView.setVisibility(View.GONE);
                        // Reset properties after the animation completes
                        fromView.setAlpha(1.0f);
                        fromView.setRotation(0);
                        fromView.setScaleX(1.0f);
                        fromView.setScaleY(1.0f);
                        fromView.setTranslationX(0);
                        fromView.setElevation(0f);
                    }
                })
                .start();

        // Scaling, Fading, Rotating, and Translating in animation for the "to" view
        toView.setAlpha(0.0f);
        toView.setScaleX(0.5f);
        toView.setScaleY(0.5f);
        toView.setRotation(isSwipeLeft ? 45f : -45f); // Initial rotation
        toView.setTranslationX(isSwipeLeft ? toView.getWidth() * 2f : -toView.getWidth() * 2f); // Move off the screen in the opposite direction
        toView.setVisibility(View.VISIBLE);
        toView.animate()
                .alpha(1.0f)
                .rotation(0) // Bring back to original rotation
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationX(0)
                .setInterpolator(new OvershootInterpolator()) // Apply overshoot effect
                .setDuration(1000) // Adjust this duration for the appearance speed
                .start();
    }



}
