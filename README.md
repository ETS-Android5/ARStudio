# AR Studio 
<p align="center">
  <img width=275 src="GIFs/logo.png">
</p>

## Get this app from 
[<img  src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height=75>](https://play.google.com/store/apps/details?id=com.ProLabs.arstudyboard) 

An implementation of ARCore [Sceneform](https://developers.google.com/sceneform/develop) for **supported** Android Phones. Sceneform has been officially open sourced by Google and this app has been built upon the latest Sceneform SDK `v1.16.0`, which was mofified to work on the latest **AndroidX** libraries and **Gradel 4.0.0**. You can find the forked repository [Here](https://github.com/Projit32/sceneform-android-sdk). 

<p align="center">
  <img  src="GIFs/features.gif" height=450>
</p>

AR Studio: Make your world your playground, draw in the air, create live photo galleries, Place notes anywhere, visualise 2D, 3D and animated models in real-time, forget projectors and theoretical explanations, analyse trends, bars and chart data, and get immersed into the world of Augmented Reality with the all-purpose Augmented Reality app with live cloud synchronisation support! This was made with simplicity in mind and has the simplest and intuitive interface to use, along with so many cool features to play with.

<p align="center">
  <img  src="GIFs/interface.gif" height=450>
</p>

See the full demo of the app and the tutorial in this [link](https://youtu.be/TLXhzyL4WCE).

> **Note :** Most of the 3D models shown here have been taken from [Google Poly](https://poly.google.com/) and
> all the 2D models are created from scrath in Android Studio and are rendered in real time only.

Pre-requisits:
1. ARCore supported **Android** phone
2. Stable internet connection
3. Firebase ``Firestore`` and ``Storage`` enabled and configured in Android Studio
4. `CloudAnchor` API key enabled in your project 


Initial Setup:
* A server, you can use xampp, where you can run PHP and host the folder `ARSR`.
* The CloudAnchor API key needs to be added in `manifest.xml` in:
```
<meta-data
       android:name="com.google.android.ar.API_KEY"
       android:value= "YOUR_API_KEY" />
```       

* Replace `YOUR_MAIN_DOMAIN_NAME` & `YOUR_TESTING_DOMAIN_NAME` to wherever you're hosting the `ARSR` folder in the following folders.

**app/src/main/java/com/ProLabs/arstudyboard/Manager/URLManager.java**

```
 public static String BaseUrl="YOUR_MAIN_DOMAIN_NAME";
 public static String DevChannelUrl="YOUR_TESTING_DOMAIN_NAME"
```                
> The purpose of having testing domain name is to host the ARSR folder somewhere where you can try out some unknown or unstable 3D models.
> I use this to segregate the working ones in the main domain and trial one in the testing. But make sure that the folder structure and the
> PHP files are intact as the APIs are same. Only the 3D models can be changed.

Primary Features (All in realtime):
1. Load static and animated 3D Models anywhere and anytime.

<p>
  <img  src="GIFs/models.gif" height=450>
  <img  src="GIFs/anim.gif" height=450>
</p>

2. 3D drawing on air.

<p>
  <img  src="GIFs/draw.gif">
</p>

3. Place 2D translucent cards in 3D space.

<p>
  <img  src="GIFs/text.gif">
</p>

4. Place 2D photos in 3D space.

<p>
  <img  src="GIFs/pics.gif">
</p>

5. Place 2D interactive graphs in 3D space.

> * Only supported extenstions are `.xls` and `.xlsx`.
> * You can put your excel files anywhere in the phone except `Downloads`.

<p>
  <img  src="GIFs/graphs.gif">
</p>

6. Create rooms to host your models, images and graphs to show them to others, live.

<p>
  <img  src="GIFs/live.gif" height=450>
</p>

Also, checkout my IGTVs on [Instagram](https://www.instagram.com/projit32/) to see the full videos of AR Studio(Former name AR Study Room) [v1](https://www.instagram.com/tv/B1lucToHrFx/?utm_source=ig_web_copy_link), [v2](https://www.instagram.com/tv/B11NjZ5n58R/?utm_source=ig_web_copy_link) and the [final teaser](https://www.instagram.com/tv/B7RVQR3pnX6/?utm_source=ig_web_copy_link).
