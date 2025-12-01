# 🎨 College Companion - Quick UI Reference

## Color Palette (Quick Copy)

### Light Mode

```xml
<color name="primary">#0052CC</color>          <!-- Deep Blue -->
<color name="secondary">#00D9A3</color>        <!-- Teal -->
<color name="tertiary">#FF6B6B</color>         <!-- Soft Red -->
<color name="background">#F8FAFB</color>       <!-- Almost White -->
<color name="text_primary">#1A1A2E</color>     <!-- Deep Navy -->
<color name="text_secondary">#6C757D</color>   <!-- Muted Gray -->
```

### Dark Mode

```xml
<color name="primary">#4080FF</color>          <!-- Brighter Blue -->
<color name="secondary">#4DFFCC</color>        <!-- Brighter Teal -->
<color name="tertiary">#FF9B9B</color>         <!-- Softer Red -->
<color name="background">#121212</color>       <!-- Dark -->
<color name="text_primary">#E1E1E1</color>     <!-- Light Gray -->
```

---

## Typography

```xml
<!-- Headline -->
android:textSize="28sp"
android:fontFamily="sans-serif-medium"
android:textColor="@color/text_primary"

<!-- Title -->
android:textSize="16sp"
android:fontFamily="sans-serif-medium"
android:textColor="@color/text_primary"

<!-- Body -->
android:textSize="14sp"
android:textColor="@color/text_secondary"
```

---

## Spacing

```xml
<!-- Small -->  android:padding="8dp"
<!-- Medium --> android:padding="16dp"
<!-- Large -->  android:padding="24dp"
<!-- XLarge --> android:padding="32dp"
```

---

## Corner Radius

```xml
<!-- Small -->  app:cardCornerRadius="8dp"
<!-- Medium --> app:cardCornerRadius="16dp"
<!-- Large -->  app:cardCornerRadius="24dp"
```

---

## Elevation

```xml
<!-- Flat -->     app:cardElevation="0dp"
<!-- Resting -->  app:cardElevation="2dp"
<!-- Raised -->   app:cardElevation="4dp"
<!-- Elevated --> app:cardElevation="8dp"
<!-- Floating --> app:cardElevation="12dp"
```

---

## Common Styles

### Card

```xml
<com.google.android.material.card.MaterialCardView
    style="@style/Widget.App.CardView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
```

### Button

```xml
<com.google.android.material.button.MaterialButton
    style="@style/Widget.App.Button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Click Me"
    app:backgroundTint="@color/primary" />
```

### FAB

```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/ic_add_24"
    app:backgroundTint="@color/secondary"
    app:fabSize="normal" />
```

---

## Animation Code

### Fade In

```kotlin
view.animate()
    .alpha(1f)
    .setDuration(300)
    .start()
```

### Scale

```kotlin
view.animate()
    .scaleX(1.1f)
    .scaleY(1.1f)
    .setDuration(200)
    .start()
```

### Pulse

```kotlin
ObjectAnimator.ofPropertyValuesHolder(
    view,
    PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f),
    PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f),
    PropertyValuesHolder.ofFloat("alpha", 0.6f, 0.0f)
).apply {
    duration = 1500
    repeatCount = ObjectAnimator.INFINITE
    start()
}
```

---

## Priority Colors

```xml
<color name="priority_urgent">#FF6B6B</color>   <!-- Red -->
<color name="priority_high">#FFA726</color>     <!-- Orange -->
<color name="priority_medium">#42A5F5</color>   <!-- Blue -->
<color name="priority_low">#66BB6A</color>      <!-- Green -->
```

---

## Icon Sizes

```xml
<!-- Small -->  android:layout_width="16dp"
<!-- Normal --> android:layout_width="24dp"
<!-- Large -->  android:layout_width="40dp"
<!-- Touch -->  android:layout_width="48dp"
```

---

## Common Drawables

```xml
@drawable/ripple_effect              <!-- Touch feedback -->
@drawable/gradient_card_background   <!-- Gradient bg -->
@drawable/bg_gradient_primary        <!-- Primary gradient -->
@drawable/circle_pulse               <!-- Pulse ring -->
@drawable/priority_indicator_high    <!-- Priority bar -->
```

---

## Common Icons

```xml
@drawable/ic_add_24          <!-- Plus icon -->
@drawable/ic_check_24        <!-- Checkmark -->
@drawable/ic_delete_24       <!-- Delete -->
@drawable/ic_time_24         <!-- Clock -->
@drawable/ic_location_24     <!-- Pin -->
@drawable/ic_notification_24 <!-- Bell -->
```

---

## Quick Layouts

### Horizontal Card List

```xml
<androidx.recyclerview.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```

### Chip Group

```xml
<com.google.android.material.chip.ChipGroup
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:singleSelection="true">
    
    <com.google.android.material.chip.Chip
        style="@style/Widget.Material3.Chip.Filter"
        android:text="Option 1" />
</com.google.android.material.chip.ChipGroup>
```

### Empty State

```xml
<LinearLayout
    android:orientation="vertical"
    android:gravity="center"
    android:padding="48dp">
    
    <TextView
        android:text="📚"
        android:textSize="72sp" />
    
    <TextView
        android:text="No items yet"
        android:textSize="18sp" />
</LinearLayout>
```

---

## File Structure

```
res/
├── layout/
│   ├── activity_main.xml        ← Main activity
│   ├── fragment_home.xml        ← Home screen
│   ├── fragment_timetable.xml   ← Timetable
│   ├── fragment_reminders.xml   ← Reminders
│   ├── item_activity.xml        ← Activity card
│   ├── item_reminder.xml        ← Reminder card
│   └── item_timetable_class.xml ← Class card
├── drawable/
│   ├── gradient_card_background.xml
│   ├── ripple_effect.xml
│   ├── ic_*.xml (20+ icons)
│   └── priority_indicator_*.xml
├── anim/
│   ├── fade_in.xml
│   ├── fade_out.xml
│   └── slide_in_*.xml
├── values/
│   ├── colors.xml   ← 50+ colors
│   ├── themes.xml   ← Material3 theme
│   └── strings.xml  ← Text resources
└── values-night/
    └── colors.xml   ← Dark mode colors
```

---

## Key Measurements

### Touch Targets

- Minimum: **48dp × 48dp**
- Recommended: **56dp × 56dp**

### Card Padding

- Compact: **12dp**
- Standard: **16dp**
- Spacious: **20dp**

### Text Line Height

- Tight: **1.2** (multiply text size)
- Normal: **1.5**
- Relaxed: **1.8**

### Screen Margins

- Phone: **16dp** - **24dp**
- Tablet: **24dp** - **32dp**

---

## Accessibility

### Content Descriptions

```xml
android:contentDescription="@string/cd_mic_button"
```

### Touch Targets

```xml
android:layout_width="48dp"
android:layout_height="48dp"
```

### Text Contrast

- Normal text: **4.5:1** minimum
- Large text: **3:1** minimum

---

## Build Commands

```bash
# Clean
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install
./gradlew installDebug
```

---

## Common Tasks

### Add new color

1. Open `values/colors.xml`
2. Add: `<color name="my_color">#RRGGBB</color>`
3. Use: `android:textColor="@color/my_color"`

### Add new icon

1. Place SVG in `drawable/`
2. Name: `ic_name_24.xml`
3. Use: `android:src="@drawable/ic_name_24"`

### Add animation

1. Create in `anim/` folder
2. Use: `android:animation="@anim/fade_in"`

### Apply theme

```xml
<style name="MyCustomStyle" parent="Widget.App.CardView">
    <item name="cardCornerRadius">20dp</item>
</style>
```

---

*Last Updated: December 2024*
*Quick Reference v2.0*
