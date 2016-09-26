/*******************************************************************************
 * <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   ~ Copyright (C)AIRIS Solutions 2015 TIIS App - Tanzania Immunization Information System App
 *   ~
 *   ~    Licensed under the Apache License, Version 2.0 (the "License");
 *   ~    you may not use this file except in compliance with the License.
 *   ~    You may obtain a copy of the License at
 *   ~
 *   ~        http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~    Unless required by applicable law or agreed to in writing, software
 *   ~    distributed under the License is distributed on an "AS IS" BASIS,
 *   ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~    See the License for the specific language governing permissions and
 *   ~    limitations under the License.
 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 ******************************************************************************/

package mobile.tiis.staging.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import mobile.tiis.staging.database.GIISContract.*;
import mobile.tiis.staging.database.SQLHandler.Tables;
import mobile.tiis.staging.util.SelectionBuilder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Melisa on 02/02/2015.
 */
public class GIISProvider extends ContentProvider {

    private static final String TAG = "GIISProvider";
    private DatabaseHandler mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int HEALTH_FACILITY = 100;
    private static final int HEALTH_FACILITY_ID = 101;
    private static final int PLACE = 200;
    private static final int PLACE_ID = 201;
    private static final int USER = 300;
    private static final int USER_ID = 301;
    private static final int CHILD = 400;
    private static final int CHILD_ID = 401;
    private static final int STATUS = 500;
    private static final int STATUS_ID = 501;
    private static final int COMMUNITY = 600;
    private static final int COMMUNITY_ID = 601;
    private static final int CHILD_WEIGHT = 700;
    private static final int CHILD_WEIGHT_ID = 701;
    private static final int NONVACCINATION_REASON = 800;
    private static final int NONVACCINATION_REASON_ID = 801;
    private static final int AGE_DEFINITIONS = 900;
    private static final int AGE_DEFINITIONS_ID = 901;
    private static final int ITEM = 1000;
    private static final int ITEM_ID = 1001;
    private static final int SCHEDULED_VACCINATION = 1100;
    private static final int SCHEDULED_VACCINATION_ID = 1101;
    private static final int DOSE = 1200;
    private static final int DOSE_ID = 1201;
    private static final int VACCINATION_APPOINTMENT = 1300;
    private static final int VACCINATION_APPOINTMENT_ID = 1301;
    private static final int VACCINATION_EVENT = 1400;
    private static final int VACCINATION_EVENT_ID = 1401;
    private static final int CHILD_SUPPLEMENTS = 1500;
    private static final int CHILD_SUPPLEMENTS_ID = 1501;
    private static final int ITEM_LOT = 1600;
    private static final int ITEM_LOT_ID = 1601;
    private static final int HEALTH_FACILITY_BALANCE = 1700;
    private static final int HEALTH_FACILITY_BALANCE_ID = 1701;


    private static final String MIME_XML = "text/xml";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GIISContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "health_facility/", HEALTH_FACILITY);
        matcher.addURI(authority, "health_facility/*", HEALTH_FACILITY_ID);

        matcher.addURI(authority, "place/", PLACE);
        matcher.addURI(authority, "place/*", PLACE_ID);

        matcher.addURI(authority, "user/", USER);
        matcher.addURI(authority, "user/*", USER_ID);

        matcher.addURI(authority, "child/", CHILD);
        matcher.addURI(authority, "child/*", CHILD_ID);

        matcher.addURI(authority, "status/", STATUS);
        matcher.addURI(authority, "status/*", STATUS_ID);

        matcher.addURI(authority, "community/", COMMUNITY);
        matcher.addURI(authority, "community/*", COMMUNITY_ID);

        matcher.addURI(authority, "child_weight/", CHILD_WEIGHT);
        matcher.addURI(authority, "child_weight/*", CHILD_WEIGHT_ID);

        matcher.addURI(authority, "nonvaccination_reason/", NONVACCINATION_REASON);
        matcher.addURI(authority, "nonvaccination_reason/*", NONVACCINATION_REASON_ID);

        matcher.addURI(authority, "age_definitions/", AGE_DEFINITIONS);
        matcher.addURI(authority, "age_definitions/*", AGE_DEFINITIONS_ID);

        matcher.addURI(authority, "item/", ITEM);
        matcher.addURI(authority, "item/*", ITEM_ID);

        matcher.addURI(authority, "scheduled_vaccination/", SCHEDULED_VACCINATION);
        matcher.addURI(authority, "scheduled_vaccination/*", SCHEDULED_VACCINATION_ID);

        matcher.addURI(authority, "dose/", DOSE);
        matcher.addURI(authority, "dose/*", DOSE_ID);

        matcher.addURI(authority, "vaccination_appointment/", VACCINATION_APPOINTMENT);
        matcher.addURI(authority, "vaccination_appointment/*", VACCINATION_APPOINTMENT_ID);

        matcher.addURI(authority, "vaccination_event/", VACCINATION_EVENT);
        matcher.addURI(authority, "vaccination_event/*", VACCINATION_EVENT_ID);

        matcher.addURI(authority, "child_supplements/", CHILD_SUPPLEMENTS);
        matcher.addURI(authority, "child_supplements/*", CHILD_SUPPLEMENTS_ID);

        matcher.addURI(authority, "item_lot/", ITEM_LOT);
        matcher.addURI(authority, "item_lot/*", ITEM_LOT_ID);

        matcher.addURI(authority, "health_facility_balance/", HEALTH_FACILITY_BALANCE);
        matcher.addURI(authority, "health_facility_balance/*", HEALTH_FACILITY_BALANCE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHandler(getContext());
        return true;
    }
    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        DatabaseHandler.deleteDatabase(context);
        mOpenHelper = new DatabaseHandler(getContext());
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                // Most cases are handled with simple SelectionBuilder
                final SelectionBuilder builder = buildExpandedSelection(uri, match);
                return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HEALTH_FACILITY:
                return HealthFacilityTable.CONTENT_TYPE;
            case HEALTH_FACILITY_ID:
                return HealthFacilityTable.CONTENT_ITEM_TYPE;
            case PLACE:
                return PlaceTable.CONTENT_TYPE;
            case PLACE_ID:
                return PlaceTable.CONTENT_ITEM_TYPE;
            case USER:
                return UserTable.CONTENT_TYPE;
            case USER_ID:
                return UserTable.CONTENT_ITEM_TYPE;
            case CHILD:
                return ChildTable.CONTENT_TYPE;
            case CHILD_ID:
                return ChildTable.CONTENT_ITEM_TYPE;
            case STATUS:
                return StatusTable.CONTENT_TYPE;
            case STATUS_ID:
                return StatusTable.CONTENT_ITEM_TYPE;
            case COMMUNITY:
                return CommunityTable.CONTENT_TYPE;
            case COMMUNITY_ID:
                return CommunityTable.CONTENT_ITEM_TYPE;
            case CHILD_WEIGHT:
                return ChildWeightTable.CONTENT_TYPE;
            case CHILD_WEIGHT_ID:
                return ChildWeightTable.CONTENT_ITEM_TYPE;
            case NONVACCINATION_REASON:
                return NonVaccinationReasonTable.CONTENT_TYPE;
            case NONVACCINATION_REASON_ID:
                return NonVaccinationReasonTable.CONTENT_ITEM_TYPE;
            case AGE_DEFINITIONS:
                return AgeDefinitionsTable.CONTENT_TYPE;
            case AGE_DEFINITIONS_ID:
                return AgeDefinitionsTable.CONTENT_ITEM_TYPE;
            case ITEM:
                return ItemTable.CONTENT_TYPE;
            case ITEM_ID:
                return ItemTable.CONTENT_ITEM_TYPE;
            case SCHEDULED_VACCINATION:
                return ScheduledVaccinationTable.CONTENT_TYPE;
            case SCHEDULED_VACCINATION_ID:
                return ScheduledVaccinationTable.CONTENT_ITEM_TYPE;
            case DOSE:
                return DoseTable.CONTENT_TYPE;
            case DOSE_ID:
                return DoseTable.CONTENT_ITEM_TYPE;
            case VACCINATION_APPOINTMENT:
                return VaccinationAppointmentTable.CONTENT_TYPE;
            case VACCINATION_APPOINTMENT_ID:
                return VaccinationAppointmentTable.CONTENT_ITEM_TYPE;
            case VACCINATION_EVENT:
                return VaccinationEventTable.CONTENT_TYPE;
            case VACCINATION_EVENT_ID:
                return VaccinationEventTable.CONTENT_ITEM_TYPE;
            case CHILD_SUPPLEMENTS:
                return ChildSupplementsTable.CONTENT_TYPE;
            case CHILD_SUPPLEMENTS_ID:
                return ChildSupplementsTable.CONTENT_ITEM_TYPE;
            case ITEM_LOT:
                return ItemLotTable.CONTENT_TYPE;
            case ITEM_LOT_ID:
                return ItemLotTable.CONTENT_ITEM_TYPE;
            case HEALTH_FACILITY_BALANCE:
                return HealthFacilityBalanceTable.CONTENT_TYPE;
            case HEALTH_FACILITY_BALANCE_ID:
                return HealthFacilityBalanceTable.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case HEALTH_FACILITY: {
                db.insertOrThrow(Tables.HEALTH_FACILITY, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return HealthFacilityTable.buildHealthFacilityUri(values.getAsString(HealthFacilityTable.ID));
            }

            case PLACE: {
                db.insertOrThrow(Tables.PLACE, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return PlaceTable.buildPlaceUri(values.getAsString(PlaceTable.ID));
            }

            case USER: {
                db.insertOrThrow(Tables.USER, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return UserTable.buildUserUri(values.getAsString(UserTable.ID));
            }
            case CHILD: {
                db.insertOrThrow(Tables.CHILD, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ChildTable.buildChildUri(values.getAsString(ChildTable.ID));
            }
            case STATUS: {
                db.insertOrThrow(Tables.STATUS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return StatusTable.buildStatusUri(values.getAsString(StatusTable.ID));
            }
            case COMMUNITY: {
                db.insertOrThrow(Tables.COMMUNITY, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return CommunityTable.buildCommunityUri(values.getAsString(CommunityTable.ID));
            }
            case CHILD_WEIGHT: {
                db.insertOrThrow(Tables.CHILD_WEIGHT, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ChildWeightTable.buildChildWeightUri(values.getAsString(ChildWeightTable.ID));
            }
            case NONVACCINATION_REASON: {
                db.insertOrThrow(Tables.NONVACCINATION_REASON, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return NonVaccinationReasonTable.buildNonVaccinationReasonUri(values.getAsString(NonVaccinationReasonTable.ID));
            }
            case AGE_DEFINITIONS: {
                db.insertOrThrow(Tables.AGE_DEFINITIONS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return AgeDefinitionsTable.buildAgeDefinitionsUri(values.getAsString(AgeDefinitionsTable.ID));
            }
            case ITEM: {
                db.insertOrThrow(Tables.ITEM, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ItemTable.buildItemUri(values.getAsString(ItemTable.ID));
            }

            case SCHEDULED_VACCINATION: {
                db.insertOrThrow(Tables.SCHEDULED_VACCINATION, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ScheduledVaccinationTable.buildScheduledVaccinationUri(values.getAsString(ScheduledVaccinationTable.ID));
            }

            case DOSE: {
                db.insertOrThrow(Tables.DOSE, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return DoseTable.buildDoseUri(values.getAsString(DoseTable.ID));
            }

            case VACCINATION_APPOINTMENT: {
                db.insertOrThrow(Tables.VACCINATION_APPOINTMENT, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return VaccinationAppointmentTable.buildVaccinationAppointmentUri(values.getAsString(VaccinationAppointmentTable.ID));
            }

            case VACCINATION_EVENT: {
                db.insertOrThrow(Tables.VACCINATION_EVENT, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return VaccinationEventTable.buildVaccinationEventUri(values.getAsString(VaccinationEventTable.ID));
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG, "delete(uri=" + uri + ")");
        if (uri == GIISContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            getContext().getContentResolver().notifyChange(uri, null, false);
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {

            case HEALTH_FACILITY: {
                return builder.table(Tables.HEALTH_FACILITY);
            }
            case HEALTH_FACILITY_ID: {
                final String healthFacilityId = HealthFacilityTable.getHealthFacilityId(uri);
                return builder.table(Tables.HEALTH_FACILITY)
                        .where(HealthFacilityTable.ID + "=?", healthFacilityId);
            }
            case PLACE: {
                return builder.table(Tables.PLACE);
            }
            case PLACE_ID: {
                final String placeId = PlaceTable.getPlaceId(uri);
                return builder.table(Tables.PLACE)
                        .where(PlaceTable.ID + "=?", placeId);
            }
            case USER: {
                return builder.table(Tables.USER);
            }
            case USER_ID: {
                final String userId = UserTable.getUserId(uri);
                return builder.table(Tables.USER)
                        .where(UserTable.ID + "=?", userId);
            }
            case CHILD: {
                return builder.table(Tables.CHILD);
            }
            case CHILD_ID: {
                final String childId = ChildTable.getChildId(uri);
                return builder.table(Tables.CHILD)
                        .where(ChildTable.ID + "=?", childId);
            }
            case STATUS: {
                return builder.table(Tables.STATUS);
            }
            case STATUS_ID: {
                final String statusId = StatusTable.getStatusId(uri);
                return builder.table(Tables.STATUS)
                        .where(StatusTable.ID + "=?", statusId);
            }
            case COMMUNITY: {
                return builder.table(Tables.COMMUNITY);
            }
            case COMMUNITY_ID: {
                final String communityId = CommunityTable.getCommunityId(uri);
                return builder.table(Tables.COMMUNITY)
                        .where(CommunityTable.ID + "=?", communityId);
            }
            case CHILD_WEIGHT: {
                return builder.table(Tables.CHILD_WEIGHT);
            }
            case CHILD_WEIGHT_ID: {
                final String childWeightId = ChildWeightTable.getChildWeightId(uri);
                return builder.table(Tables.CHILD_WEIGHT)
                        .where(ChildWeightTable.ID + "=?", childWeightId);
            }
            case NONVACCINATION_REASON: {
                return builder.table(Tables.NONVACCINATION_REASON);
            }
            case NONVACCINATION_REASON_ID: {
                final String id = NonVaccinationReasonTable.getNonVaccinationReasonId(uri);
                return builder.table(Tables.NONVACCINATION_REASON)
                        .where(NonVaccinationReasonTable.ID + "=?", id);
            }
            case AGE_DEFINITIONS: {
                return builder.table(Tables.AGE_DEFINITIONS);
            }
            case AGE_DEFINITIONS_ID: {
                final String id = AgeDefinitionsTable.getAgeDefinitionsId(uri);
                return builder.table(Tables.AGE_DEFINITIONS)
                        .where(AgeDefinitionsTable.ID + "=?", id);
            }
            case ITEM: {
                return builder.table(Tables.ITEM);
            }
            case ITEM_ID: {
                final String id = ItemTable.getItemId(uri);
                return builder.table(Tables.ITEM)
                        .where(ItemTable.ID + "=?", id);
            }

            case SCHEDULED_VACCINATION: {
                return builder.table(Tables.SCHEDULED_VACCINATION);
            }
            case SCHEDULED_VACCINATION_ID: {
                final String id = ScheduledVaccinationTable.getScheduledVaccinationdId(uri);
                return builder.table(Tables.SCHEDULED_VACCINATION)
                        .where(ItemTable.ID + "=?", id);
            }
            case DOSE: {
                return builder.table(Tables.DOSE);
            }
            case DOSE_ID: {
                final String id = DoseTable.getDoseId(uri);
                return builder.table(Tables.DOSE)
                        .where(DoseTable.ID + "=?", id);
            }

            case VACCINATION_APPOINTMENT: {
                return builder.table(Tables.VACCINATION_APPOINTMENT);
            }
            case VACCINATION_APPOINTMENT_ID: {
                final String id = VaccinationAppointmentTable.getVaccinationAppointmentId(uri);
                return builder.table(Tables.VACCINATION_APPOINTMENT)
                        .where(VaccinationAppointmentTable.ID + "=?", id);
            }

            case VACCINATION_EVENT: {
                return builder.table(Tables.VACCINATION_EVENT);
            }
            case VACCINATION_EVENT_ID: {
                final String id = VaccinationEventTable.getVaccinationEventId(uri);
                return builder.table(Tables.VACCINATION_EVENT)
                        .where(VaccinationEventTable.ID + "=?", id);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case HEALTH_FACILITY: {
                return builder.table(Tables.HEALTH_FACILITY);
            }
            case HEALTH_FACILITY_ID: {
                final String healthFacilityId = HealthFacilityTable.getHealthFacilityId(uri);
                return builder.table(Tables.HEALTH_FACILITY)
                        .where(HealthFacilityTable.ID + "=?", healthFacilityId);
            }
            case PLACE: {
                return builder.table(Tables.PLACE);
            }
            case PLACE_ID: {
                final String placeId = PlaceTable.getPlaceId(uri);
                return builder.table(Tables.PLACE)
                        .where(PlaceTable.ID + "=?", placeId);
            }
            case USER: {
                return builder.table(Tables.USER);
            }
            case USER_ID: {
                final String userId = UserTable.getUserId(uri);
                return builder.table(Tables.USER)
                        .where(UserTable.ID + "=?", userId);
            }
            case CHILD: {
                return builder.table(Tables.CHILD);
            }
            case CHILD_ID: {
                final String childId = ChildTable.getChildId(uri);
                return builder.table(Tables.CHILD)
                        .where(ChildTable.ID + "=?", childId);
            }
            case STATUS: {
                return builder.table(Tables.STATUS);
            }
            case STATUS_ID: {
                final String statusId = StatusTable.getStatusId(uri);
                return builder.table(Tables.STATUS)
                        .where(StatusTable.ID + "=?", statusId);
            }
            case COMMUNITY: {
                return builder.table(Tables.COMMUNITY);
            }
            case COMMUNITY_ID: {
                final String communityId = CommunityTable.getCommunityId(uri);
                return builder.table(Tables.COMMUNITY)
                        .where(CommunityTable.ID + "=?", communityId);
            }
            case CHILD_WEIGHT: {
                return builder.table(Tables.CHILD_WEIGHT);
            }
            case CHILD_WEIGHT_ID: {
                final String childWeightId = ChildWeightTable.getChildWeightId(uri);
                return builder.table(Tables.CHILD_WEIGHT)
                        .where(ChildWeightTable.ID + "=?", childWeightId);
            }
            case NONVACCINATION_REASON: {
                return builder.table(Tables.NONVACCINATION_REASON);
            }
            case NONVACCINATION_REASON_ID: {
                final String id = NonVaccinationReasonTable.getNonVaccinationReasonId(uri);
                return builder.table(Tables.NONVACCINATION_REASON)
                        .where(NonVaccinationReasonTable.ID + "=?", id);
            }
            case AGE_DEFINITIONS: {
                return builder.table(Tables.AGE_DEFINITIONS);
            }
            case AGE_DEFINITIONS_ID: {
                final String id = AgeDefinitionsTable.getAgeDefinitionsId(uri);
                return builder.table(Tables.AGE_DEFINITIONS)
                        .where(AgeDefinitionsTable.ID + "=?", id);
            }
            case ITEM: {
                return builder.table(Tables.ITEM);
            }
            case ITEM_ID: {
                final String id = ItemTable.getItemId(uri);
                return builder.table(Tables.ITEM)
                        .where(ItemTable.ID + "=?", id);
            }
            case SCHEDULED_VACCINATION: {
                return builder.table(Tables.SCHEDULED_VACCINATION);
            }
            case SCHEDULED_VACCINATION_ID: {
                final String id = ScheduledVaccinationTable.getScheduledVaccinationdId(uri);
                return builder.table(Tables.SCHEDULED_VACCINATION)
                        .where(ItemTable.ID + "=?", id);
            }
            case DOSE: {
                return builder.table(Tables.DOSE);
            }
            case DOSE_ID: {
                final String id = DoseTable.getDoseId(uri);
                return builder.table(Tables.DOSE)
                        .where(DoseTable.ID + "=?", id);
            }

            case VACCINATION_APPOINTMENT: {
                return builder.table(Tables.VACCINATION_APPOINTMENT);
            }
            case VACCINATION_APPOINTMENT_ID: {
                final String id = VaccinationAppointmentTable.getVaccinationAppointmentId(uri);
                return builder.table(Tables.VACCINATION_APPOINTMENT)
                        .where(VaccinationAppointmentTable.ID + "=?", id);
            }

            case VACCINATION_EVENT: {
                return builder.table(Tables.VACCINATION_EVENT);
            }
            case VACCINATION_EVENT_ID: {
                final String id = VaccinationEventTable.getVaccinationEventId(uri);
                return builder.table(Tables.VACCINATION_EVENT)
                        .where(VaccinationEventTable.ID + "=?", id);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}


