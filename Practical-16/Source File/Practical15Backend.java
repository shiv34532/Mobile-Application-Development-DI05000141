package com.example.arpitapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Practical15Backend extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Vibrator vibrator;

    private TextView tvValX, tvValY, tvValZ;
    private TextView tvOrientationBadge, tvSensorStatusBadge, tvBallCount, tvShakeHint, tvSensorDetails;
    private Button btnAddBall, btnResetArena;
    private FrameLayout arenaContainer;

    private BallPhysicsView physicsView;
    private int shakeCount = 0;
    private long lastShakeTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical15_frontend);

        // Dynamic Status Bar: Indigo (#4F46E5) with bright white icons
        setupStatusBar(Color.parseColor("#4F46E5"), true);

        // Safe Area Insets: Ensure header text starts safely below status bar & camera cutout
        View headerBanner = findViewById(R.id.p15HeaderBanner);
        if (headerBanner != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerBanner, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                int extraTopPadding = (int) (14 * getResources().getDisplayMetrics().density);
                v.setPadding(
                        v.getPaddingLeft(),
                        systemBars.top + extraTopPadding,
                        v.getPaddingRight(),
                        v.getPaddingBottom()
                );
                return insets;
            });
        }

        // Initialize UI Elements
        tvValX = findViewById(R.id.tvValX);
        tvValY = findViewById(R.id.tvValY);
        tvValZ = findViewById(R.id.tvValZ);
        tvOrientationBadge = findViewById(R.id.tvOrientationBadge);
        tvSensorStatusBadge = findViewById(R.id.tvSensorStatusBadge);
        tvBallCount = findViewById(R.id.tvBallCount);
        tvShakeHint = findViewById(R.id.tvShakeHint);
        tvSensorDetails = findViewById(R.id.tvSensorDetails);
        btnAddBall = findViewById(R.id.btnAddBall);
        btnResetArena = findViewById(R.id.btnResetArena);
        arenaContainer = findViewById(R.id.arenaContainer);

        // Attach custom BallPhysicsView inside the FrameLayout arena
        physicsView = new BallPhysicsView(this);
        arenaContainer.addView(physicsView);

        // Vibrator service for shake/collision haptic feedback
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize Hardware Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelerometerSensor != null) {
            tvSensorStatusBadge.setText("📳 Sensor Active");
            displaySensorHardwareSpecs();
        } else {
            tvSensorStatusBadge.setText("⚠️ Sensor Missing");
            tvSensorStatusBadge.setBackgroundResource(R.drawable.bg_pill_red);
            tvSensorStatusBadge.setTextColor(Color.parseColor("#991B1B"));
            tvSensorDetails.setText("Hardware Accelerometer is not available on this device / emulator.");
            Toast.makeText(this, "Accelerometer not found on device", Toast.LENGTH_LONG).show();
        }

        // Add ball action
        btnAddBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (physicsView != null) {
                    physicsView.addNewBall();
                    updateBallCountUI();
                }
            }
        });

        // Reset arena action
        btnResetArena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (physicsView != null) {
                    physicsView.resetArena();
                    updateBallCountUI();
                    Toast.makeText(Practical15Backend.this, "Balls recentered in arena", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateBallCountUI();
    }

    private void updateBallCountUI() {
        if (physicsView != null) {
            tvBallCount.setText(physicsView.getBallCount() + " Balls Active");
        }
    }

    private void displaySensorHardwareSpecs() {
        if (accelerometerSensor == null) return;
        String specs = "• Sensor: " + accelerometerSensor.getName() + "\n"
                + "• Vendor: " + accelerometerSensor.getVendor() + "\n"
                + "• Version: " + accelerometerSensor.getVersion() + "\n"
                + "• Power: " + accelerometerSensor.getPower() + " mA\n"
                + "• Max Range: " + accelerometerSensor.getMaximumRange() + " m/s²\n"
                + "• Resolution: " + accelerometerSensor.getResolution() + " m/s²";
        tvSensorDetails.setText(specs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometerSensor != null) {
            // SENSOR_DELAY_GAME provides ultra-smooth 60fps response for physics
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // ==========================================
    // SensorEventListener Callbacks
    // ==========================================
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        // event.values[0]: Acceleration along X axis (Left/Right)
        // event.values[1]: Acceleration along Y axis (Up/Down)
        // event.values[2]: Acceleration along Z axis (Gravity / Face)
        float rawX = event.values[0];
        float rawY = event.values[1];
        float rawZ = event.values[2];

        // Update telemetry HUD
        tvValX.setText(String.format(Locale.getDefault(), "%.2f", rawX));
        tvValY.setText(String.format(Locale.getDefault(), "%.2f", rawY));
        tvValZ.setText(String.format(Locale.getDefault(), "%.2f", rawZ));

        // Detect device physical posture / orientation
        updateOrientationBadge(rawX, rawY, rawZ);

        // Detect Shake Motion Gesture
        detectShakeGesture(rawX, rawY, rawZ);

        // Pass gravity/tilt force to physics view
        // Invert X because phone tilted right yields negative X in Android sensor
        float forceX = -rawX;
        float forceY = rawY;
        if (physicsView != null) {
            physicsView.updateTiltForces(forceX, forceY);
        }
    }

    private void updateOrientationBadge(float x, float y, float z) {
        if (Math.abs(x) < 1.8f && Math.abs(y) < 1.8f && z > 7.5f) {
            tvOrientationBadge.setText("🟢 Level Balanced (Flat)");
            tvOrientationBadge.setBackgroundResource(R.drawable.bg_pill_green);
            tvOrientationBadge.setTextColor(Color.parseColor("#065F46"));
        } else if (x > 3.0f) {
            tvOrientationBadge.setText("🔵 Tilted Left");
            tvOrientationBadge.setBackgroundResource(R.drawable.bg_pill_blue);
            tvOrientationBadge.setTextColor(Color.parseColor("#1E40AF"));
        } else if (x < -3.0f) {
            tvOrientationBadge.setText("🔵 Tilted Right");
            tvOrientationBadge.setBackgroundResource(R.drawable.bg_pill_blue);
            tvOrientationBadge.setTextColor(Color.parseColor("#1E40AF"));
        } else if (y > 5.0f) {
            tvOrientationBadge.setText("🟣 Upright Portrait");
            tvOrientationBadge.setBackgroundResource(R.drawable.bg_pill_blue);
            tvOrientationBadge.setTextColor(Color.parseColor("#1E40AF"));
        } else if (y < -3.5f) {
            tvOrientationBadge.setText("🟠 Inverted / Down");
            tvOrientationBadge.setBackgroundResource(R.drawable.bg_pill_gray);
            tvOrientationBadge.setTextColor(Color.parseColor("#475569"));
        } else {
            tvOrientationBadge.setText("🟡 Dynamic Motion");
            tvOrientationBadge.setBackgroundResource(R.drawable.bg_pill_blue);
            tvOrientationBadge.setTextColor(Color.parseColor("#1E40AF"));
        }
    }

    private void detectShakeGesture(float x, float y, float z) {
        float netAcceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;
        long currentTime = System.currentTimeMillis();

        if (netAcceleration > 10.0f) { // Shake force threshold
            if (currentTime - lastShakeTime > 600) { // Throttle 600ms
                lastShakeTime = currentTime;
                shakeCount++;
                tvShakeHint.setText("🎉 SHAKE DETECTED! Total: " + shakeCount + " (Balls Scattered!)");

                // Trigger subtle haptic buzz
                triggerHapticFeedback();

                // Scatter balls in physics arena!
                if (physicsView != null) {
                    physicsView.scatterBalls();
                }
            }
        }
    }

    private void triggerHapticFeedback() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(80);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No-op for accelerometer
    }

    /**
     * Dynamic Status Bar Configurator:
     * - Dark background (e.g. #4F46E5): Icons are WHITE (setAppearanceLightStatusBars(false))
     * - Light background (e.g. #FFFFFF): Icons are DARK/BLACK (setAppearanceLightStatusBars(true))
     */
    private void setupStatusBar(int statusBarColor, boolean isDarkBackground) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(statusBarColor);
        }
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(!isDarkBackground);
        }
    }

    // =========================================================================
    // CUSTOM 2D BALL PHYSICS CANVAS VIEW (Rolls, bounces & collides via tilt)
    // =========================================================================
    public static class BallPhysicsView extends View {

        private static class Ball {
            float x, y;
            float vx = 0, vy = 0;
            float radius;
            int primaryColor;
            int highlightColor;

            Ball(float x, float y, float radius, int primaryColor, int highlightColor) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.primaryColor = primaryColor;
                this.highlightColor = highlightColor;
            }
        }

        private final List<Ball> balls = new ArrayList<>();
        private final Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint targetRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random();

        private float tiltForceX = 0;
        private float tiltForceY = 0;
        private boolean initialized = false;

        private static final int[][] PALETTE = {
                {Color.parseColor("#10B981"), Color.parseColor("#A7F3D0")}, // Emerald Green
                {Color.parseColor("#0284C7"), Color.parseColor("#BAE6FD")}, // Sky Blue
                {Color.parseColor("#F59E0B"), Color.parseColor("#FDE68A")}, // Amber Orange
                {Color.parseColor("#8B5CF6"), Color.parseColor("#DDD6FE")}, // Purple
                {Color.parseColor("#EC4899"), Color.parseColor("#FBCFE8")}, // Pink
                {Color.parseColor("#06B6D4"), Color.parseColor("#CFFAFE")}, // Cyan
                {Color.parseColor("#EF4444"), Color.parseColor("#FECACA")}  // Coral Red
        };

        public BallPhysicsView(Context context) {
            super(context);
            shadowPaint.setColor(Color.parseColor("#50000000"));
            shadowPaint.setStyle(Paint.Style.FILL);

            gridPaint.setColor(Color.parseColor("#1E293B"));
            gridPaint.setStrokeWidth(1.5f);
            gridPaint.setStyle(Paint.Style.STROKE);

            targetRingPaint.setColor(Color.parseColor("#334155"));
            targetRingPaint.setStrokeWidth(2f);
            targetRingPaint.setStyle(Paint.Style.STROKE);
        }

        public void updateTiltForces(float fx, float fy) {
            this.tiltForceX = fx;
            this.tiltForceY = fy;
            invalidate();
        }

        public int getBallCount() {
            return balls.size();
        }

        public void addNewBall() {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            float r = (24 + random.nextInt(12)) * getResources().getDisplayMetrics().density;
            float spawnX = w / 2f + (random.nextFloat() - 0.5f) * 80;
            float spawnY = h / 2f + (random.nextFloat() - 0.5f) * 80;
            int[] pair = PALETTE[random.nextInt(PALETTE.length)];

            Ball newBall = new Ball(spawnX, spawnY, r, pair[0], pair[1]);
            newBall.vx = (random.nextFloat() - 0.5f) * 15;
            newBall.vy = (random.nextFloat() - 0.5f) * 15;
            balls.add(newBall);
            invalidate();
        }

        public void resetArena() {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            balls.clear();
            initDefaultBalls(w, h);
            invalidate();
        }

        public void scatterBalls() {
            for (Ball b : balls) {
                b.vx += (random.nextFloat() - 0.5f) * 45;
                b.vy += (random.nextFloat() - 0.5f) * 45;
            }
            invalidate();
        }

        private void initDefaultBalls(int w, int h) {
            balls.clear();
            float density = getResources().getDisplayMetrics().density;

            float[][] defaultConfigs = {
                    {w * 0.35f, h * 0.40f, 26 * density, 0},
                    {w * 0.65f, h * 0.40f, 24 * density, 1},
                    {w * 0.50f, h * 0.60f, 28 * density, 2},
                    {w * 0.30f, h * 0.70f, 22 * density, 3},
                    {w * 0.70f, h * 0.70f, 25 * density, 4}
            };

            for (float[] cfg : defaultConfigs) {
                int paletteIdx = (int) cfg[3] % PALETTE.length;
                balls.add(new Ball(cfg[0], cfg[1], cfg[2], PALETTE[paletteIdx][0], PALETTE[paletteIdx][1]));
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (!initialized && w > 0 && h > 0) {
                initDefaultBalls(w, h);
                initialized = true;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            if (width <= 0 || height <= 0) return;

            // 1. Draw Arena Center Target Rings & Crosshair
            float centerX = width / 2f;
            float centerY = height / 2f;
            canvas.drawCircle(centerX, centerY, 60, targetRingPaint);
            canvas.drawCircle(centerX, centerY, 120, targetRingPaint);
            canvas.drawLine(centerX - 30, centerY, centerX + 30, centerY, targetRingPaint);
            canvas.drawLine(centerX, centerY - 30, centerX, centerY + 30, targetRingPaint);

            // 2. Physics Step: Apply acceleration, damping, and wall bouncing
            float gravityScale = 0.45f;
            float damping = 0.982f;
            float bounceElasticity = 0.72f;

            for (Ball ball : balls) {
                // Apply tilt forces from Accelerometer
                ball.vx = (ball.vx + tiltForceX * gravityScale) * damping;
                ball.vy = (ball.vy + tiltForceY * gravityScale) * damping;

                // Move position
                ball.x += ball.vx;
                ball.y += ball.vy;

                // Left & Right Wall Boundaries
                if (ball.x - ball.radius < 0) {
                    ball.x = ball.radius;
                    ball.vx = -ball.vx * bounceElasticity;
                } else if (ball.x + ball.radius > width) {
                    ball.x = width - ball.radius;
                    ball.vx = -ball.vx * bounceElasticity;
                }

                // Top & Bottom Wall Boundaries
                if (ball.y - ball.radius < 0) {
                    ball.y = ball.radius;
                    ball.vy = -ball.vy * bounceElasticity;
                } else if (ball.y + ball.radius > height) {
                    ball.y = height - ball.radius;
                    ball.vy = -ball.vy * bounceElasticity;
                }
            }

            // 3. Ball-to-Ball Elastic Collision Simulation
            int size = balls.size();
            for (int i = 0; i < size; i++) {
                Ball b1 = balls.get(i);
                for (int j = i + 1; j < size; j++) {
                    Ball b2 = balls.get(j);

                    float dx = b2.x - b1.x;
                    float dy = b2.y - b1.y;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    float minDist = b1.radius + b2.radius;

                    if (dist < minDist && dist > 0.001f) {
                        // Separate balls to prevent sticking
                        float overlap = 0.5f * (minDist - dist);
                        float nx = dx / dist;
                        float ny = dy / dist;

                        b1.x -= nx * overlap;
                        b1.y -= ny * overlap;
                        b2.x += nx * overlap;
                        b2.y += ny * overlap;

                        // Velocity exchange along collision normal
                        float kx = b1.vx - b2.vx;
                        float ky = b1.vy - b2.vy;
                        float p = 2 * (nx * kx + ny * ky) / 2.0f; // equal mass

                        b1.vx -= p * nx * 0.85f;
                        b1.vy -= p * ny * 0.85f;
                        b2.vx += p * nx * 0.85f;
                        b2.vy += p * ny * 0.85f;
                    }
                }
            }

            // 4. Render Balls with 3D Radial Gradient & Soft Shadows
            for (Ball ball : balls) {
                // Drop shadow
                canvas.drawCircle(ball.x + 4, ball.y + 6, ball.radius, shadowPaint);

                // 3D Spherical Radial Gradient (light source from top-left)
                RadialGradient gradient = new RadialGradient(
                        ball.x - ball.radius * 0.32f,
                        ball.y - ball.radius * 0.35f,
                        ball.radius * 1.15f,
                        ball.highlightColor,
                        ball.primaryColor,
                        Shader.TileMode.CLAMP
                );
                ballPaint.setShader(gradient);
                canvas.drawCircle(ball.x, ball.y, ball.radius, ballPaint);
            }

            // Keep smooth 60fps simulation loop running
            postInvalidateOnAnimation();
        }
    }
}
