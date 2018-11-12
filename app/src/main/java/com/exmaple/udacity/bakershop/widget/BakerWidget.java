package com.exmaple.udacity.bakershop.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.service.BakerWidgetIntentService;
import com.exmaple.udacity.bakershop.ui.RecipeActivity;

public class BakerWidget extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(String recipeName,Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baker_widget);
        views.setTextViewText(R.id.textViewWidgetTitle, recipeName);
        Intent intent=new Intent(context,WidgetListViewRemoteService.class);
        views.setRemoteAdapter(R.id.listViewrecipeIngredient,intent);
        Intent onclickIntent=new Intent(context, RecipeActivity.class);
        PendingIntent pendingIntent= TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(onclickIntent)
                .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.listViewrecipeIngredient,pendingIntent);
        views.setEmptyView(R.id.listViewrecipeIngredient,R.id.emptyWidgetErrorMessageView);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.listViewrecipeIngredient);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BakerWidgetIntentService.startActionLoadDefaultRecipe(context);
    }

    public static void onUpdate(String recipeName,Context context,AppWidgetManager appWidgetManager,
                                int[] appWidgetIds) {
        for(int appWidgetId:appWidgetIds) {
            updateAppWidget(recipeName,context,appWidgetManager,appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

