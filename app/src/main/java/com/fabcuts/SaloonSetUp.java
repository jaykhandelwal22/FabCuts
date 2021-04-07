package com.fabcuts;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SaloonSetUp extends AppCompatActivity {

    ActionBar actionBar;
    ImageView ivupload;
    Button bupload;
    String userChoosenTask;
    Uri imgUri;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public  String photofilename="StoreImage.jpg";
    public final String APP_TAG = "Fab_Cuts";
    private StorageReference mStorageRef;
    ProgressDialog dialog;
    public static final String FB_STORAGE_PATH = "salon_images/";
    private RecyclerView mBlogList,mBlogList2;
    FirebaseRecyclerAdapter<ListServices,BlogViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<ListBarbers,BlogViewHolder2> firebaseRecyclerAdapter2;
    private DatabaseReference mDatabase,mDatabase2;
    Button bcreateservice,baddbrber;
    String sname,bname;
    int scost=0,stime=0;
    EditText etsname,etscost,etstime,etbname;
    private static final int requestCod = 831;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_saloon_set_up);

        actionBar=getSupportActionBar();

        assert actionBar != null;
        actionBar.setTitle("Salon SetUp Page");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mBlogList=findViewById(R.id.rvsalonservices);
        mBlogList2=findViewById(R.id.rvbarbers);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        mBlogList2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));



        ivupload=findViewById(R.id.ivupload);
        bupload=findViewById(R.id.bupload);
        bcreateservice=findViewById(R.id.bcreateservice);
        etscost=findViewById(R.id.etservicecost);
        etsname=findViewById(R.id.etservicename);
        etstime=findViewById(R.id.etservicetime);
        etbname=findViewById(R.id.etbname);
        baddbrber=findViewById(R.id.baddbarber);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        ivupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        baddbrber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bname=etbname.getText().toString();
                if (!bname.equals("")){
                    SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    String random;

                    random=FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                            .child("barbers").push().getKey();

                    FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                            .child("barbers").child(bname).child("time").setValue(0);

                    FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                            .child("barbers").child(bname).child("status").setValue(1);

                    FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                            .child("barbers").child(bname).child("name").setValue(bname)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SaloonSetUp.this, "Added to List", Toast.LENGTH_SHORT).show();
                                    etbname.setText("");
                                }
                            });
                }
            }
        });

        bupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri!=null){
                    uploadImage(imgUri);
                }
                else {
                    Toast.makeText(SaloonSetUp.this, "Select an Image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bcreateservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname=etsname.getText().toString();
                scost= Integer.parseInt(etscost.getText().toString());
                stime=Integer.parseInt(etstime.getText().toString());

                if (sname!=""){
                    if (scost!=0){
                        if (stime!=0){
                            SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                            FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                                    .child("services").child(sname).child("servicename").setValue(sname);
                            FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                                    .child("services").child(sname).child("price").setValue(scost);
                            FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                                    .child("services").child(sname).child("estimatedtime").setValue(stime)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SaloonSetUp.this, "New Service added", Toast.LENGTH_SHORT).show();
                                            etscost.setText("");
                                            etsname.setText("");
                                            etstime.setText("");
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(SaloonSetUp.this, "Enter Time of service", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(SaloonSetUp.this, "Enter Cost of service", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SaloonSetUp.this, "Enter Name of service", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadservices();
        loadBarbers();
    }

    private void loadBarbers() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase2= FirebaseDatabase.getInstance().getReference().child("saloons").
                child(sharedPreferences.getString("phone",null)).child("barbers");

        firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<ListBarbers,BlogViewHolder2>
                (ListBarbers.class, R.layout.layout_barber_list,BlogViewHolder2.class, mDatabase2) {

            @Override
            protected void populateViewHolder(BlogViewHolder2 viewHolder, ListBarbers model, int position) {
                viewHolder.setName(model.getName());
            }
        };
        mBlogList2.setAdapter(firebaseRecyclerAdapter2);
    }


    private void loadservices() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null)).child("services");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListServices,BlogViewHolder>
                (ListServices.class, R.layout.layout_services,BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final ListServices model, int position) {
                viewHolder.setServiceName(model.getServicename());
                viewHolder.setServicePrice(model.getPrice());
                viewHolder.badd.setVisibility(View.GONE);
                viewHolder.bremove.setVisibility(View.VISIBLE);
                viewHolder.bremove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        FirebaseDatabase.getInstance().getReference().child("saloons").
                                child(sharedPreferences.getString("phone",null)).child("services").child(model.servicename).removeValue();
                        Toast.makeText(SaloonSetUp.this, "Service removed", Toast.LENGTH_SHORT).show();

                    }
                });
            }


        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView badd;
        TextView bremove;
        int i=0;



        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            badd=mView.findViewById(R.id.tvbadd);
            bremove=mView.findViewById(R.id.tvremove);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());

                }
            });

        }


        public void setServiceName(String serviceName){
            TextView post_aval=mView.findViewById(R.id.tvsname);
            post_aval.setText(serviceName);

        }

        public void setServicePrice(int servicePrice){
            TextView post_aval=mView.findViewById(R.id.tvsprice);
            post_aval.setText("Rs."+servicePrice+"");

        }

    }




    public static class BlogViewHolder2 extends RecyclerView.ViewHolder {
        View mView;

        public BlogViewHolder2(View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setName(String bname){
            TextView post_aval=mView.findViewById(R.id.tvbname);
            post_aval.setText(bname);

        }

    }



    private void selectImage() {

        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(SaloonSetUp.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(SaloonSetUp.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,getPhotoFileUri(photofilename));
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, requestCod);
            Toast.makeText(this,"Permission Required", Toast.LENGTH_SHORT).show();

        }
        else {
            startActivityForResult(intent, REQUEST_CAMERA);
        }

    }
    private Uri getPhotoFileUri(String photofilename) {
        if (isExternalStorageAvailable()) {
            File mediaStoreDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            if (!mediaStoreDir.exists() && !mediaStoreDir.mkdirs()) {

            }
            return Uri.fromFile(new File(mediaStoreDir.getPath() + File.separator + photofilename));

        }
        return null;
    }
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

    }

    private void onCaptureImageResult(Intent data) {

        imgUri=getPhotoFileUri(photofilename);

        try {
            if(imgUri!=null) {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                ivupload.setVisibility(View.VISIBLE);
                ivupload.setImageURI(imgUri);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null && data.getData() != null){
            imgUri = data.getData();
            ivupload.setVisibility(View.VISIBLE);
            ivupload.setImageURI(imgUri);
        }

        else if(data!=null ) {
            Bitmap b = (Bitmap) data.getExtras().get(("data"));
        }

        try {
            if(imgUri!=null) {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void
    uploadImage(Uri uri) {
        dialog.setTitle("Uploading..please wait");
        dialog.setCancelable(false);
        dialog.show();

        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.JPEG, 40,bytes);
        String path = MediaStore.Images.Media.insertImage(SaloonSetUp.this.getContentResolver(), bm, imgUri.getPath(), null);
        Uri uploaduri = Uri.parse(path);

        final StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(uploaduri));

        ref.putFile(uploaduri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(), "image uploaded", Toast.LENGTH_LONG).show();
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        String Url = Objects.requireNonNull(task.getResult()).toString();
                        FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                                .child("url").push().child("url").setValue(Url).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toast.makeText(SaloonSetUp.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("uploaded " + (int) progress + "%");
                    }
                });
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),SaloonDashboard.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
