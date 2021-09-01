package com.example.bookapp.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.MyApplication;
import com.example.bookapp.PdfDetailActivity;
import com.example.bookapp.PdfEditActivity;
import com.example.bookapp.databinding.RowPdfAdminBinding;
import com.example.bookapp.filters.FilterPdfAdmin;
import com.example.bookapp.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    //context
    private Context context;
    //arrayList to hold list of data of type Modelpdf
    public ArrayList<ModelPdf>pdfArrayList,filterList;

    //view binding row_pdf_admin.xml
    private RowPdfAdminBinding binding ;

    private FilterPdfAdmin filter ;

    private static final String TAG = "PDF_ADAPTER_TAG";

    //progress
    private ProgressDialog progressDialog;

    //constructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind layout using view binding
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        /*Get data ,set data ,handle clicks */


        //get data
        ModelPdf model = pdfArrayList.get(position);
        String pdfId =model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        //we need to convert timestamp to dd//mm/yy format
        String formatedDate = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formatedDate);

        //we will
        //load Further details like category,pdf form url,pdf siz  in Separate function
        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryTv);
        MyApplication.loadPdfFormUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar
        );
        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv
        );

        //handle click, show dialog with options 1) Edit,2) Delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });

        //handle book/pdf click,open pdf details page,pass pdf/book id to get details of it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        //options to show in dialog
        String[] options = {"Edit","Delete"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                       //handle dialog option click
                       if (which==0){
                           //Edit clicked,open  PdfEditActivity to edit the book info
                           Intent intent = new Intent(context, PdfEditActivity.class);
                           intent.putExtra("bookId", bookId);
                           context.startActivity(intent);
                       }
                       else if (which==1){
                           //Delete clicked
                           MyApplication.deleteBook(
                                   context,
                                   ""+bookId ,
                                   ""+bookUrl,
                                   ""+bookTitle
                           );
                       }

                    }
                })
                .show();

    }







    @Override
    public int getItemCount() {
        return pdfArrayList.size(); //return number of records 1 list size
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterPdfAdmin(filterList,this);
        }
        return filter;
    }

    /*View Holder class for row_pdf_admin.xml*/
    class HolderPdfAdmin extends RecyclerView.ViewHolder{


            //UI views of row_pdf_admin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoryTv,sizeTv,dateTv ;
        ImageButton moreBtn;


            public HolderPdfAdmin(@NonNull View itemView) {
                super(itemView);

                //init ui views
                pdfView = binding.pdfView;
                progressBar = binding.progressBar;
                titleTv = binding.titleTv;
                descriptionTv = binding.descriptionTv;
                categoryTv = binding.categoryTv;
                sizeTv = binding.sizeTv;
                dateTv = binding.dateTv;
                moreBtn = binding.moreBtn;
            }
        }
}