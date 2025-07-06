
# Lupa - Project Extractor

Search your images with text. Application indexes local images and extracts information from them
using [MLKit](https://developers.google.com/ml-kit/guides) models to allow keyword searching across images.

Helps sift through a giant library of images to find something specific without having to upload to
external services such as Google Photos etc.

While the current implementation uses MLKit models underneath(optimized for mobile devices),
it might be possible to swap for open source models.

# Quick Start
- Download and set up Android Studio 
  - Download: [https://developer.android.com/studio](https://developer.android.com/studio) 
  - Install: [https://developer.android.com/studio/install](https://developer.android.com/studio/install)
  - Configure: [https://developer.android.com/studio/intro/studio-config](https://developer.android.com/studio/intro/studio-config)
- Clone the Repository or Download as ZIP
- Open project in Android Studio and:
  - Run a build: `ctrl/cmd + F9`
  - Start app in connected device/emulator: `shift + F10`
  - (Optionally) start in debug mode: `shift + F9 `

*Optionally* Download from the Play Store below

<a href="https://play.google.com/store/apps/details?id=com.drbrosdev.extractor" target="_blank">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" width="250"/>
</a>

Refer to the `docs` folder for details on app architecture, project structure and etc.

# Usage

The application will request permission to read on device images. After onboarding is finished
a background process will start indexing all images to store their searchable information.

After it is done, it is possible to search and filter to find results. Its also possible to tag images
with any matter of info - when searching for these images will pop up. 

Frequent searches can be saved as albums.

## License
MIT License

Copyright (c) 2024 Extractor by DrBrosDev & Co

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
