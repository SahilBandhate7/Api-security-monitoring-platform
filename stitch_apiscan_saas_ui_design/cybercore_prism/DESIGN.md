# Design System Specification: The Clinical Intelligence Framework

## 1. Overview & Creative North Star
**Creative North Star: "The Digital Surgeon"**
In the world of API security, "Modern" and "Trustworthy" are often misinterpreted as "Generic" and "Safe." This design system rejects the typical boxed-in SaaS aesthetic. Instead, we embrace a **Clinical Intelligence** framework—a high-end editorial approach that mimics the precision of a medical instrument and the clarity of a premium financial journal.

The system breaks the "template" look by utilizing **intentional asymmetry** and **tonal layering**. We replace rigid, 1px-bordered grids with "floating" content modules and dramatic whitespace. By combining the technical rigor of *Inter* with the sophisticated, wide-set architecture of *Manrope*, we create an interface that doesn't just display data—it narrates security insights with authority.

---

## 2. Colors & Surface Philosophy
The palette is rooted in a deep, intellectual spectrum of blues and functional neutrals. We move away from "flat" design into a world of perceived depth through color alone.

### The "No-Line" Rule
**Strict Mandate:** Traditional 1px solid borders are prohibited for sectioning. 
Boundaries are defined exclusively through background color shifts. To separate a sidebar from a main content area, use `surface-container-low` (#eff4ff) against the `surface` (#f8f9ff) background. This creates a "soft edge" that feels integrated rather than walled off.

### Surface Hierarchy & Nesting
Treat the UI as a physical stack of premium materials.
*   **Base Layer:** `background` (#f8f9ff) — The canvas.
*   **Section Layer:** `surface-container-low` (#eff4ff) — For secondary regions or sidebars.
*   **Action Layer (Cards):** `surface-container-lowest` (#ffffff) — Used for primary interactive modules to create a "lifted" effect.
*   **Elevated Layer:** `surface-container-high` (#dce9ff) — Reserved for active states or temporary overlays.

### The "Glass & Gradient" Rule
To elevate primary actions (like "Run Scan"), do not use flat colors. Use a **Signature Texture**: A linear gradient from `primary` (#004ac6) to `primary_container` (#2563eb) at a 135-degree angle. For floating navigation or modal overlays, apply **Glassmorphism**: Use `surface_container_lowest` at 80% opacity with a `24px` backdrop-blur to allow underlying data hints to bleed through.

---

## 3. Typography
We use a dual-font strategy to balance technical utility with editorial prestige.

*   **Display & Headlines (Manrope):** Chosen for its geometric precision. Use `display-lg` (3.5rem) for high-level security scores. The wide aperture of Manrope conveys a sense of openness and "clean" code.
*   **Body & Labels (Inter):** The workhorse. Use `body-md` (0.875rem) for all technical API logs. It is optimized for high readability in dense data environments.
*   **Identity Hierarchy:** 
    *   **Authoritative:** Large `headline-sm` headers in `on_surface` (#0d1c2e) suggest a "no-nonsense" diagnostic tone.
    *   **Supporting:** Use `label-md` in `on_surface_variant` (#434655) for metadata, ensuring the eye focuses on the data (the signal) rather than the labels (the noise).

---

## 4. Elevation & Depth
Depth in this system is a result of **Tonal Layering**, not structural shadows.

*   **The Layering Principle:** Place a `surface-container-lowest` card on top of a `surface-container-low` background. The slight shift in hex value creates a natural perception of height without the clutter of a drop shadow.
*   **Ambient Shadows:** If a component must float (e.g., a critical alert popover), use an **Ambient Shadow**: `0px 12px 32px rgba(13, 28, 46, 0.06)`. The shadow color is derived from `on_surface` to mimic natural light dispersion.
*   **The "Ghost Border":** For input fields where containment is required for accessibility, use the `outline_variant` (#c3c6d7) at **15% opacity**. It should be felt, not seen.
*   **Roundedness:** Adhere to the `lg` scale (1rem / 16px) for cards and `DEFAULT` (0.5rem / 8px) for buttons and inputs. This softens the technical "edge" of the API data.

---

## 5. Components

### Buttons
*   **Primary:** Gradient (`primary` to `primary_container`), `DEFAULT` (8px) rounding, white text. No shadow.
*   **Secondary:** `surface_container_highest` background with `on_primary_fixed_variant` text.
*   **Tertiary:** Ghost style; text only in `primary` (#004ac6). Underline only on hover.

### Risk Chips (Semantic Precision)
Instead of standard badges, use "Intensity Chips":
*   **High Risk:** `error_container` (#ffdad6) background with `on_error_container` (#93000a) text.
*   **Medium Risk:** `tertiary_fixed` (#ffdbcd) background with `tertiary` (#943700) text.
*   **Low Risk:** `surface_container_high` background with `primary` text.

### Data Cards & Lists
*   **Rule:** Forbid the use of divider lines.
*   **Implementation:** Separate list items using `12px` of vertical whitespace. For complex API endpoint lists, use alternating background tints: `surface` vs `surface_container_low`.

### Specialized Components: The "Security Pulse"
*   **Endpoint Health Bar:** A slim, 4px track using `outline_variant` with a glowing `primary` indicator.
*   **Scan Progress:** Utilize the `Glassmorphism` rule—a blurred overlay that sits atop the dashboard while a scan is active, keeping the user focused on the process.

---

## 6. Do’s and Don’ts

### Do
*   **DO** use whitespace as a functional tool. If a screen feels "busy," increase the padding-to-content ratio rather than adding borders.
*   **DO** use `manrope` for numbers. In a security tool, data is the hero; give it the editorial weight of a headline.
*   **DO** use "Surface Nesting" to group related API parameters.

### Don't
*   **DON'T** use 100% black (#000000). Use `on_surface` (#0d1c2e) for all text to maintain the sophisticated slate-blue tone.
*   **DON'T** use traditional 1px gray borders. It makes the software look like a legacy utility rather than a modern security suite.
*   **DON'T** use high-saturation reds for everything. Reserve `error` (#ba1a1a) for "Critical Action Required" only. For "High Risk" data, use the more nuanced `on_error_container` shades.

---