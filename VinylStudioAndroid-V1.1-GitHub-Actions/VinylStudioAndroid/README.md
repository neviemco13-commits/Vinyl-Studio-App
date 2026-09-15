# Vinyl Studio Android — V1.1

A first Android prototype for a vinyl artwork editor.

## V1.1 features

- Choose an image from the device
- Crop/zoom and position artwork
- Vinyl colour and transparency controls
- Gloss, groove, grain and highlight effects
- Multiple vinyl shapes
- Multiple centre-label styles
- Label size control
- Optional artwork on the centre label
- Spinning preview
- PNG export

## Build the APK online with GitHub Actions

This project includes a ready-to-use GitHub Actions workflow at:

`.github/workflows/build-apk.yml`

### Steps

1. Create a GitHub account at https://github.com/ if you do not already have one.
2. Create a new repository, for example `VinylStudioAndroid`.
3. Upload the **contents of this project folder** to the repository (so `settings.gradle`, `app/`, and `.github/` are at the repository root).
4. Open the repository's **Actions** tab.
5. Select **Build Vinyl Studio APK**.
6. Click **Run workflow**.
7. Wait for the workflow to finish successfully.
8. Open the completed workflow run and scroll to **Artifacts**.
9. Download `VinylStudio-debug-apk`.
10. Extract the downloaded ZIP and install `app-debug.apk` on your Android phone.

The workflow uses Java 17 and Gradle 8.7, matching the Android Gradle Plugin version used by this project. It builds the debug APK in GitHub's cloud runner and uploads it as a workflow artifact.

## Local build

If you later install Android Studio, you can open this project directly and build it normally.
