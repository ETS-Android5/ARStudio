package com.ProLabs.arstudyboard.Manager;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ProLabs.arstudyboard.MainActivity;
import com.ProLabs.arstudyboard.RenderableItems.AudioItem;
import com.ProLabs.arstudyboard.RenderableItems.GraphItem;
import com.ProLabs.arstudyboard.RenderableItems.ImageItem;
import com.ProLabs.arstudyboard.RenderableItems.ModelItem;
import com.ProLabs.arstudyboard.RenderableItems.TextItem;
import com.ProLabs.arstudyboard.Utility.LiveObject;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {

    private MainActivity mainActivity;
    private FirebaseFirestore firestoreReference;
    private DocumentReference documentReference;
    private volatile CollectionReference collectionReferenceImage,collectionReferenceText,collectionReferenceModel,collectionReferenceGraph,temp, collectionReferenceAudio;
    private String roomNumber,number;
    private StorageReference storageReference=FirebaseStorage.getInstance().getReference();
    private ListenerRegistration ImageListener,TextListener,ModelListener,GraphListener, AudioListener;

    public FirebaseManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        firestoreReference=FirebaseFirestore.getInstance();
    }

    public void destroyReferences()
    {
        try {
            ModelListener.remove();
            ImageListener.remove();
            TextListener.remove();
            GraphListener.remove();
            AudioListener.remove();
            firestoreReference.waitForPendingWrites();
            firestoreReference.terminate();
        }
        catch (Exception e)
        {
            Log.v("Firebase Manager",e.toString());
        }
    }

    public boolean isRoomInitialized(){return roomNumber!=null;}

    public void initializeRoom(String room) throws InterruptedException {
        roomNumber="Room_"+room;
        number=room;
        documentReference= firestoreReference.collection("ARS").document(roomNumber);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if(!documentSnapshot.exists())
            {
                createNewRoom();
            }
            else {
                try {
                    checkRoomAge(documentSnapshot.getString("Created On"));
                } catch (Exception e) {
                    showErrorFlashbar(e.getMessage());
                }
            }

        })
        .addOnFailureListener(documentReference->showErrorFlashbar("Error fetching the room."));

        initializeReferences();
    }

    private void createNewRoom()
    {
        Map<String,Object> roomMetaData= new HashMap<>();
        roomMetaData.put("Created On",new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date()));
        documentReference.set(roomMetaData)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Room Created, the session will expire in 24 hours. Tap on the Live Button to join the room");
                })

                .addOnFailureListener(e -> {
                    showErrorFlashbar("Error in Room Creation");
                });
    }

    private void initializeReferences()
    {
        collectionReferenceModel=documentReference.collection("Models");
        collectionReferenceText=documentReference.collection("Texts");
        collectionReferenceGraph=documentReference.collection("Graphs");
        collectionReferenceImage=documentReference.collection("Images");
        collectionReferenceAudio=documentReference.collection("Audio");
    }

    public void insertModel(ModelItem modelItem)
    {
        collectionReferenceModel.add(modelItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceModel.document(docId);
                    documentReference.update("documentID",docId);
                    modelItem.setdocumentID(task.getResult().getId());
                });
    }

    public void insertText(TextItem textItem)
    {
        collectionReferenceText.add(textItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceText.document(docId);
                    documentReference.update("documentID",docId);
                    textItem.setdocumentID(docId);
                });
        //setOnChangeListeners();
    }

    public void insertGraph(GraphItem graphItem)
    {
        collectionReferenceGraph.add(graphItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceGraph.document(docId);
                    documentReference.update("documentID",docId);
                    graphItem.setdocumentID(docId);
                });
        //setOnChangeListeners();
    }

    public void insertImage(ImageItem imageItem)
    {
        collectionReferenceImage.add(imageItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceImage.document(docId);
                    documentReference.update("documentID",docId);
                    imageItem.setdocumentID(docId);
                });
    }
    public void insertAudio(AudioItem audioItem)
    {
        collectionReferenceAudio.add(audioItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceAudio.document(docId);
                    documentReference.update("documentID",docId);
                    audioItem.setdocumentID(docId);
                });
    }

    public void uploadImage(byte[] imagebytes, ImageItem imageItem)
    {
        String filename=Long.toHexString(System.currentTimeMillis()+(long)Math.random()*10000000)+".png";
        StorageReference childref=storageReference.child(roomNumber+"/"+filename);
        UploadTask uploadTask=childref.putBytes(imagebytes);
        uploadTask.addOnProgressListener(snapshot -> {
            Toast.makeText(mainActivity, (snapshot.getBytesTransferred()/snapshot.getTotalByteCount()*100)+"% Uploaded", Toast.LENGTH_SHORT).show();
        }).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return childref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                imageItem.setDownloadURL(downloadUri.toString());
                imageItem.setFileURL(roomNumber+"/"+filename);
                insertImage(imageItem);

            } else {
                showErrorFlashbar("Error in image uploading");
            }
        });
    }
    public void uploadAudio(Uri uri, AudioItem audioItem) {
        String filename=Long.toHexString(System.currentTimeMillis()+ (long) (Math.random() * 10000000))+"."+getMimeType(uri);
        StorageReference childref=storageReference.child(roomNumber+"/"+filename);

        UploadTask uploadTask=childref.putFile(uri);

        uploadTask.addOnProgressListener(snapshot -> {
            Toast.makeText(mainActivity, (snapshot.getBytesTransferred()/snapshot.getTotalByteCount()*100)+"% Uploaded", Toast.LENGTH_SHORT).show();
        }).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return childref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                audioItem.setDownloadURL(downloadUri.toString());
                audioItem.setFileURL(roomNumber+"/"+filename);
                insertAudio(audioItem);

            } else {
                showErrorFlashbar("Error in image uploading");
            }
        });
    }

    public String getMimeType(Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(mainActivity.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    private void showLiveFlashbar(String message)
    {
            mainActivity.showLiveFlashbar(message,false);
    }
    private void showErrorFlashbar(String message)
    {
            mainActivity.showErrorFlashbar(message);
    }
    private void showBusyLiveFlashbar(String message) { mainActivity.showLiveFlashbar(message,true); }
    
    public void setOnChangeListeners()
    {
        ModelListener=collectionReferenceModel.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                ModelItem item=documentSnapshot.toObject(ModelItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.MODEL,item);
                        if(!presentNode(item.getcloudAnchorID()) && dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
        TextListener=collectionReferenceText.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                TextItem item=documentSnapshot.toObject(TextItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.TEXT,item);
                        if(!presentNode(item.getcloudAnchorID()) && dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
        ImageListener=collectionReferenceImage.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                ImageItem item=documentSnapshot.toObject(ImageItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.PICTURE,item);
                        if(!presentNode(item.getcloudAnchorID()) && dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
        GraphListener=collectionReferenceGraph.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                GraphItem item=documentSnapshot.toObject(GraphItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.GRAPH,item);
                        if(!presentNode(item.getcloudAnchorID())&& dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
        AudioListener=collectionReferenceAudio.addSnapshotListener(mainActivity, (queryDocumentSnapshots, e)->{
            if (e != null) {
               return;
            }
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                AudioItem item=documentSnapshot.toObject(AudioItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.AUDIO,item);
                        if(!presentNode(item.getcloudAnchorID()) && dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        //TODO : check release audio for deleted node
                        if(presentNode(item.getcloudAnchorID())) {
                            mainActivity.deleteAudioById(item.cloudAnchorID);
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        }
                        break;
                }
            }
        });
    }

    public void deleteAnchor(String anchorID, MainActivity.AnchorType type)
    {
        //delete with query
        switch (type)
        {
            case MODEL:
                temp=collectionReferenceModel;
                break;
            case PICTURE:
                temp=collectionReferenceImage;
                break;
            case TEXT:
                temp=collectionReferenceText;
                break;
            case GRAPH:
                temp=collectionReferenceGraph;
                break;
            case AUDIO:
                temp=collectionReferenceAudio;
                break;
        }

        temp.whereEqualTo("cloudAnchorID",anchorID).get().addOnSuccessListener(queryDocumentSnapshots -> { for (DocumentSnapshot document : queryDocumentSnapshots) {
            if(type== MainActivity.AnchorType.PICTURE)
            {
                StorageReference childref=storageReference.child(document.toObject(ImageItem.class).getFileURL());
                childref.delete();
            }
            else if(type== MainActivity.AnchorType.AUDIO)
            {
                StorageReference childref=storageReference.child(document.toObject(AudioItem.class).getFileURL());
                childref.delete();
            }
            temp.document(document.getId()).delete();}});


    }

    private boolean presentNode(String cloudAnchorID)
    {
        return mainActivity.placedNode.containsKey(cloudAnchorID);
    }

    private void checkRoomAge(String RoomDate) throws InterruptedException, ParseException {
        double age=timeDifference(RoomDate);
        if(age>24) {
            initializeReferences();
            Thread image = new Thread(() -> {
                collectionReferenceImage.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ImageItem item = documentSnapshot.toObject(ImageItem.class);
                        deleteAnchor(item.cloudAnchorID, MainActivity.AnchorType.PICTURE);
                    }
                });
            });
            image.start();
            Thread graph = new Thread(() -> {
                collectionReferenceGraph.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        GraphItem item = documentSnapshot.toObject(GraphItem.class);
                        deleteAnchor(item.cloudAnchorID, MainActivity.AnchorType.GRAPH);
                    }
                });
            });
            graph.start();
            Thread text = new Thread(() -> {
                collectionReferenceText.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        TextItem item = documentSnapshot.toObject(TextItem.class);
                        deleteAnchor(item.cloudAnchorID, MainActivity.AnchorType.TEXT);
                    }
                });

            });
            text.start();
            Thread model = new Thread(() -> {
                collectionReferenceModel.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ModelItem item = documentSnapshot.toObject(ModelItem.class);
                        deleteAnchor(item.cloudAnchorID, MainActivity.AnchorType.MODEL);
                    }
                });

            });
            model.start();
            Thread audio = new Thread(()->{
                collectionReferenceAudio.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        AudioItem item = documentSnapshot.toObject(AudioItem.class);
                        deleteAnchor(item.cloudAnchorID, MainActivity.AnchorType.MODEL);
                    }
                });
            });
            audio.start();

            image.join();
            graph.join();
            text.join();
            model.join();
            audio.join();

            documentReference.delete().addOnSuccessListener(aVoid -> {
                showBusyLiveFlashbar("Previous session of this room has expired. Creating a new session");
                try {
                    mainActivity.liveObjects.clear();
                    initializeRoom(number);
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorFlashbar("Error Creating Room : "+e.getMessage());
                }
            });
        }
        else {
            showLiveFlashbar("Room joined, this session will expire in "+new DecimalFormat("#0.00").format(24.0-age)+" hours. Press the live button to start live session.");
        }

    }

    private double timeDifference(String roomDate) throws ParseException {
        Date room=new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").parse(roomDate);
        Date now = new Date();
        long diff=now.getTime()-room.getTime();
        return (double)diff/3600000;
    }



}
