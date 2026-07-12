# Design System Documentation: The Sentinel Perspective

## 1. Overview & Creative North Star
The design system for this platform is anchored by a Creative North Star we call **"The Intelligent Lens."** In the world of API security, raw data is chaotic; our role is to provide a sophisticated, editorial-grade clarity that feels authoritative yet effortless. 

This is not a "utility-first" interface; it is a "curated-first" experience. We move beyond the rigid, boxy layouts of traditional SaaS by utilizing **intentional asymmetry, deep tonal layering, and high-contrast typography.** By treating the dashboard as an editorial canvas, we signal to the user that the platform isn't just scanning code—it’s understanding it. The interface should feel like a premium workspace where security professionals come to think, not just to click.

---

## 2. Colors: Tonal Architecture
The palette transitions away from harsh "clinical" whites into a more sophisticated spectrum of `surface` and `surface-container` tiers. 

### The "No-Line" Rule
**Explicit Instruction:** Designers are prohibited from using 1px solid borders to define sections or containers. Boundaries must be established through:
*   **Background Shifts:** Using `surface-container-low` for secondary sections sitting on a `surface` background.
*   **Tonal Transitions:** Defining logic blocks through color blocks rather than outlines.

### Surface Hierarchy & Nesting
Treat the UI as physical layers of "Synthetic Glass." 
*   **Base:** `surface` (#f7f9fb)
*   **Level 1 (Sections):** `surface-container` (#e8eff3) 
*   **Level 2 (Active Modules):** `surface-container-low` (#f0f4f7)
*   **Level 3 (Floating Details):** `surface-container-lowest` (#ffffff)

### The "Glass & Gradient" Rule
To escape the "template" look, primary actions and high-level summaries should utilize **Glassmorphism**. Use semi-transparent `primary` or `surface` colors with a `backdrop-filter: blur(20px)`. 

### Signature Textures
Main CTAs must use a subtle linear gradient from `primary` (#0053db) to `primary_dim` (#0048c1) at a 135-degree angle. This provides a tactile "soul" to the action, suggesting depth and importance.

---

## 3. Typography: Editorial Authority
We utilize a dual-font strategy to balance technical precision with executive-level presentation.

*   **Display & Headlines (Manrope):** Used for data visualization titles and hero headings. The geometric nature of Manrope provides an "architectural" feel.
    *   *Scale:* `display-lg` (3.5rem) down to `headline-sm` (1.5rem).
*   **Body & UI (Inter):** Used for all functional elements, code snippets, and labels. Inter is the workhorse that ensures legibility in dense security logs.
    *   *Scale:* `title-lg` (1.375rem) for card headers; `body-md` (0.875rem) for standard text.

**Visual Hierarchy Tip:** Always pair a `display-md` headline with a `label-md` (Inter, uppercase, tracked out +10%) to create a high-contrast, professional "editorial" look.

---

## 4. Elevation & Depth: Tonal Layering
Traditional drop shadows are often a crutch for poor layout. In this system, we prioritize **Tonal Lift**.

*   **The Layering Principle:** Instead of a shadow, place a `surface-container-lowest` card on a `surface-container` background. The subtle shift from #ffffff to #e8eff3 creates a soft, natural lift.
*   **Ambient Shadows:** Where floating elements (like Modals or Tooltips) are required, use an extra-diffused shadow: `box-shadow: 0 20px 40px rgba(42, 52, 57, 0.06)`. Note the use of `on_surface` (#2a3439) as the shadow tint rather than pure black.
*   **The "Ghost Border" Fallback:** If accessibility requires a stroke (e.g., in high-contrast modes), use the `outline_variant` (#a9b4b9) at **20% opacity**. Never use 100% opaque borders.

---

## 5. Components: Functional Primitives

### Buttons
*   **Primary:** Gradient fill (`primary` to `primary_dim`), `rounded-md` (0.75rem), white text.
*   **Secondary:** `surface-container-high` fill, no border, `on_surface` text.
*   **Tertiary:** No fill, `primary` text, subtle `surface-container-lowest` hover state.

### Chips (Analysis Tags)
*   Used for API endpoints or risk levels. Use `secondary_container` with `on_secondary_container` text. Avoid "pill" shapes; use `rounded-sm` (0.25rem) to maintain the "code-block" aesthetic.

### Input Fields
*   **Base State:** `surface-container-lowest` background with a subtle "Ghost Border."
*   **Focus State:** Background remains white, but the "Ghost Border" transitions to 100% opacity `primary`.

### Cards & Lists (The Divider-Free Rule)
*   **Forbid dividers.** To separate list items, use increased vertical whitespace (16px–24px) or a alternating `surface-container-low` background on hover.
*   **Cards:** Use `rounded-lg` (1.0rem) for top-level dashboard cards to create a friendly, accessible feel.

### Specialized Component: "The Trace Node"
A custom component for this platform representing an API call. It should use a `primary_container` background with a `primary` left-accent bar (4px) to denote "active analysis."

---

## 6. Do’s and Don’ts

### Do
*   **Do** use asymmetrical layouts. A narrow sidebar (240px) next to a wide, expansive content area creates a "Master-Detail" relationship that feels premium.
*   **Do** use `surface_bright` to highlight the most critical security vulnerabilities.
*   **Do** prioritize white space. If a layout feels "crowded," remove a container background rather than shrinking the text.

### Don't
*   **Don't** use "Alert Red" (#ff0000). Use our `error` (#9f403d) and `error_container` (#fe8983) for a more sophisticated, "muted urgency."
*   **Don't** use medical icons (crosses, stethoscopes). Use "Lens" (analysis), "Shield" (security), and "Terminal" (code) metaphors.
*   **Don't** use 1px dividers. If you feel the need to separate content, use an 8px–16px gap of empty space or a shift in surface tone.

---

## 7. Roundedness Scale
*   **Default:** `0.5rem` (Standard UI components)
*   **Medium (md):** `0.75rem` (Buttons, Inputs)
*   **Large (lg):** `1.0rem` (Primary Dashboard Cards)
*   **Extra Large (xl):** `1.5rem` (Modals and Hero sections)