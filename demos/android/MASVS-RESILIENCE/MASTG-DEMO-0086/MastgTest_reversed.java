package org.owasp.mastestapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.autofill.HintConstants;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.PointerIconCompat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.owasp.mastestapp.MastgTest;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0016\b\u0007\u0018\u00002\u00020\u0001:\u00019B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u001a\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\t2\b\u0010\u000f\u001a\u0004\u0018\u00010\tH\u0002J\u000e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J0\u0010\u0011\u001a\u00020\f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u000e\u001a\u00020\t2\u0014\u0010\u0014\u001a\u0010\u0012\u0004\u0012\u00020\u0013\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0015H\u0002J.\u0010\u0016\u001a\u00020\f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u000e\u001a\u00020\t2\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u0015H\u0002J\u000e\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J2\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u001a\u001a\u00020\t2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u0002J\u0016\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\u000e\u0010 \u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u0002J\u0018\u0010!\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\tH\u0002J\u0016\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u000b2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\u000e\u0010%\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u0002J\u000e\u0010&\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u0002J\u000e\u0010'\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u000e\u0010(\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u001c\u0010)\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u001c\u0010+\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u001c\u0010-\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\f\u0010.\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u001c\u0010/\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u001e\u00101\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\t2\f\u00102\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u0002J\n\u00103\u001a\u0004\u0018\u00010\tH\u0002J\u001a\u00104\u001a\u00020\t*\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u000e\u001a\u00020\tH\u0002J\u001a\u00105\u001a\u00020\t*\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u000e\u001a\u00020\tH\u0002J\u000e\u00106\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u0002J\u0016\u00107\u001a\u00020\t2\f\u00108\u001a\b\u0012\u0004\u0012\u00020\t0\u000bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006:"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "mastgTest", "", "queryBuildProperties", "", "Lorg/owasp/mastestapp/MastgTest$QueryResult;", "queryBuildValue", HintConstants.AUTOFILL_HINT_NAME, "value", "queryTelephonyProperties", "queryTelephonyValue", "telephonyManager", "Landroid/telephony/TelephonyManager;", "block", "Lkotlin/Function1;", "queryTelephonyIntValue", "", "queryPackageChecks", "buildPrefixResults", "label", "prefixes", "packages", "queryLauncherPackages", "pm", "Landroid/content/pm/PackageManager;", "queryRunningServices", "isPackageInstalled", "packageName", "getInstalledPackages", "Landroid/content/pm/PackageInfo;", "emulatorPackagePrefixes", "emulatorPackageExact", "queryOpenGlProperties", "openGlUnavailableResults", "buildIndicators", "buildQueries", "telephonyIndicators", "telephonyQueries", "packageIndicators", "packageQueries", "openGlIndicators", "openGlQueries", "containsAny", "tokens", "safeBuildSerial", "findValue", "findDisplayValue", "ensureTelephonyPermissions", "telephonyPermissionNote", "missingPermissions", "QueryResult", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final boolean shouldRunInMainThread() {
        return true;
    }

    public final String mastgTest() throws SecurityException {
        String indicatorSummary;
        List missingPermissions = ensureTelephonyPermissions();
        List buildQueries = queryBuildProperties();
        List telephonyQueries = queryTelephonyProperties();
        List packageQueries = queryPackageChecks();
        List openGlQueries = queryOpenGlProperties();
        List allQueries = CollectionsKt.plus((Collection) CollectionsKt.plus((Collection) CollectionsKt.plus((Collection) buildQueries, (Iterable) telephonyQueries), (Iterable) packageQueries), (Iterable) openGlQueries);
        List indicators = CollectionsKt.plus((Collection) CollectionsKt.plus((Collection) CollectionsKt.plus((Collection) buildIndicators(buildQueries), (Iterable) telephonyIndicators(telephonyQueries)), (Iterable) packageIndicators(packageQueries)), (Iterable) openGlIndicators(openGlQueries));
        String queryOutput = CollectionsKt.joinToString$default(allQueries, "\n", null, null, 0, null, new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.mastgTest$lambda$0((MastgTest.QueryResult) obj);
            }
        }, 30, null);
        String permissionNote = telephonyPermissionNote(missingPermissions);
        if (indicators.isEmpty()) {
            indicatorSummary = "Indicators matched in this run: none";
        } else {
            indicatorSummary = "Indicators matched in this run: " + CollectionsKt.joinToString$default(indicators, ", ", null, null, 0, null, null, 62, null);
        }
        String output = "Queried properties:\n" + queryOutput + "\n\n" + indicatorSummary + permissionNote;
        Log.i("MASTG-TEST", output);
        return output;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence mastgTest$lambda$0(QueryResult it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getName() + "=" + it.getDisplayValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J)\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0013\u001a\u00020\u0014HÖ\u0001J\t\u0010\u0015\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\u0016"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$QueryResult;", "", HintConstants.AUTOFILL_HINT_NAME, "", "rawValue", "displayValue", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getName", "()Ljava/lang/String;", "getRawValue", "getDisplayValue", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    static final /* data */ class QueryResult {
        private final String displayValue;
        private final String name;
        private final String rawValue;

        public static /* synthetic */ QueryResult copy$default(QueryResult queryResult, String str, String str2, String str3, int i, Object obj) {
            if ((i & 1) != 0) {
                str = queryResult.name;
            }
            if ((i & 2) != 0) {
                str2 = queryResult.rawValue;
            }
            if ((i & 4) != 0) {
                str3 = queryResult.displayValue;
            }
            return queryResult.copy(str, str2, str3);
        }

        /* renamed from: component1, reason: from getter */
        public final String getName() {
            return this.name;
        }

        /* renamed from: component2, reason: from getter */
        public final String getRawValue() {
            return this.rawValue;
        }

        /* renamed from: component3, reason: from getter */
        public final String getDisplayValue() {
            return this.displayValue;
        }

        public final QueryResult copy(String name, String rawValue, String displayValue) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(displayValue, "displayValue");
            return new QueryResult(name, rawValue, displayValue);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof QueryResult)) {
                return false;
            }
            QueryResult queryResult = (QueryResult) other;
            return Intrinsics.areEqual(this.name, queryResult.name) && Intrinsics.areEqual(this.rawValue, queryResult.rawValue) && Intrinsics.areEqual(this.displayValue, queryResult.displayValue);
        }

        public int hashCode() {
            return (((this.name.hashCode() * 31) + (this.rawValue == null ? 0 : this.rawValue.hashCode())) * 31) + this.displayValue.hashCode();
        }

        public String toString() {
            return "QueryResult(name=" + this.name + ", rawValue=" + this.rawValue + ", displayValue=" + this.displayValue + ")";
        }

        public QueryResult(String name, String rawValue, String displayValue) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(displayValue, "displayValue");
            this.name = name;
            this.rawValue = rawValue;
            this.displayValue = displayValue;
        }

        public final String getName() {
            return this.name;
        }

        public final String getRawValue() {
            return this.rawValue;
        }

        public final String getDisplayValue() {
            return this.displayValue;
        }
    }

    private final List<QueryResult> queryBuildProperties() {
        return CollectionsKt.listOf((Object[]) new QueryResult[]{queryBuildValue("Build.BOARD", Build.BOARD), queryBuildValue("Build.BRAND", Build.BRAND), queryBuildValue("Build.DEVICE", Build.DEVICE), queryBuildValue("Build.FINGERPRINT", Build.FINGERPRINT), queryBuildValue("Build.MODEL", Build.MODEL), queryBuildValue("Build.MANUFACTURER", Build.MANUFACTURER), queryBuildValue("Build.PRODUCT", Build.PRODUCT), queryBuildValue("Build.HARDWARE", Build.HARDWARE), queryBuildValue("Build.ID", Build.ID), queryBuildValue("Build.RADIO", Build.getRadioVersion()), queryBuildValue("Build.SERIAL", safeBuildSerial()), queryBuildValue("Build.TAGS", Build.TAGS), queryBuildValue("Build.USER", Build.USER)});
    }

    private final QueryResult queryBuildValue(String name, String value) {
        String displayValue = value == null ? "<null>" : value;
        return new QueryResult(name, value, displayValue);
    }

    private final List<QueryResult> queryTelephonyProperties() {
        boolean hasTelephony = this.context.getPackageManager().hasSystemFeature("android.hardware.telephony");
        Object systemService = this.context.getSystemService(HintConstants.AUTOFILL_HINT_PHONE);
        TelephonyManager telephonyManager = systemService instanceof TelephonyManager ? (TelephonyManager) systemService : null;
        return CollectionsKt.listOf((Object[]) new QueryResult[]{new QueryResult("PackageManager.FEATURE_TELEPHONY", String.valueOf(hasTelephony), String.valueOf(hasTelephony)), queryTelephonyValue(telephonyManager, "TelephonyManager.getLine1Number", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.queryTelephonyProperties$lambda$1((TelephonyManager) obj);
            }
        }), queryTelephonyValue(telephonyManager, "TelephonyManager.getNetworkCountryIso", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.queryTelephonyProperties$lambda$2((TelephonyManager) obj);
            }
        }), queryTelephonyIntValue(telephonyManager, "TelephonyManager.getNetworkType", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Integer.valueOf(MastgTest.queryTelephonyProperties$lambda$3((TelephonyManager) obj));
            }
        }), queryTelephonyValue(telephonyManager, "TelephonyManager.getNetworkOperator", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda4
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.queryTelephonyProperties$lambda$4((TelephonyManager) obj);
            }
        }), queryTelephonyValue(telephonyManager, "TelephonyManager.getNetworkOperatorName", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda5
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.queryTelephonyProperties$lambda$5((TelephonyManager) obj);
            }
        }), queryTelephonyIntValue(telephonyManager, "TelephonyManager.getPhoneType", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda6
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Integer.valueOf(MastgTest.queryTelephonyProperties$lambda$6((TelephonyManager) obj));
            }
        }), queryTelephonyValue(telephonyManager, "TelephonyManager.getSimCountryIso", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda7
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.queryTelephonyProperties$lambda$7((TelephonyManager) obj);
            }
        }), queryTelephonyValue(telephonyManager, "TelephonyManager.getVoiceMailNumber", new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda8
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.queryTelephonyProperties$lambda$8((TelephonyManager) obj);
            }
        })});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String queryTelephonyProperties$lambda$1(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getLine1Number();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String queryTelephonyProperties$lambda$2(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getNetworkCountryIso();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int queryTelephonyProperties$lambda$3(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getNetworkType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String queryTelephonyProperties$lambda$4(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getNetworkOperator();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String queryTelephonyProperties$lambda$5(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getNetworkOperatorName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int queryTelephonyProperties$lambda$6(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getPhoneType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String queryTelephonyProperties$lambda$7(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getSimCountryIso();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String queryTelephonyProperties$lambda$8(TelephonyManager it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getVoiceMailNumber();
    }

    private final QueryResult queryTelephonyValue(TelephonyManager telephonyManager, String name, Function1<? super TelephonyManager, String> block) {
        if (telephonyManager == null) {
            return new QueryResult(name, null, "<unavailable>");
        }
        try {
            String value = block.invoke(telephonyManager);
            return new QueryResult(name, value, value == null ? "<null>" : value);
        } catch (SecurityException e) {
            return new QueryResult(name, null, "<permission denied>");
        }
    }

    private final QueryResult queryTelephonyIntValue(TelephonyManager telephonyManager, String name, Function1<? super TelephonyManager, Integer> block) {
        if (telephonyManager == null) {
            return new QueryResult(name, null, "<unavailable>");
        }
        try {
            String value = String.valueOf(block.invoke(telephonyManager).intValue());
            return new QueryResult(name, value, value);
        } catch (SecurityException e) {
            return new QueryResult(name, null, "<permission denied>");
        }
    }

    private final List<QueryResult> queryPackageChecks() throws SecurityException {
        PackageManager pm = this.context.getPackageManager();
        List results = new ArrayList();
        List prefixes = emulatorPackagePrefixes();
        Intrinsics.checkNotNull(pm);
        List installedPackages = getInstalledPackages(pm);
        results.add(new QueryResult("PackageManager.getInstalledPackages.count", String.valueOf(installedPackages.size()), String.valueOf(installedPackages.size())));
        List $this$map$iv = installedPackages;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            PackageInfo it = (PackageInfo) item$iv$iv;
            destination$iv$iv.add(it.packageName);
        }
        List installedNames = (List) destination$iv$iv;
        results.addAll(buildPrefixResults("InstalledPackagePrefix", prefixes, installedNames));
        List launcherPackages = queryLauncherPackages(pm);
        results.add(new QueryResult("PackageManager.queryIntentActivities(MAIN/LAUNCHER).count", String.valueOf(launcherPackages.size()), String.valueOf(launcherPackages.size())));
        results.addAll(buildPrefixResults("LauncherPackagePrefix", prefixes, launcherPackages));
        for (String pkg : emulatorPackageExact()) {
            String installed = String.valueOf(isPackageInstalled(pm, pkg));
            results.add(new QueryResult("PackageManager.hasPackage:" + pkg, installed, installed));
        }
        List runningServices = queryRunningServices();
        results.add(new QueryResult("ActivityManager.getRunningServices.count", String.valueOf(runningServices.size()), String.valueOf(runningServices.size())));
        List $this$filter$iv = runningServices;
        Collection destination$iv$iv2 = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String it2 = (String) element$iv$iv;
            PackageManager pm2 = pm;
            List prefixes2 = prefixes;
            List installedPackages2 = installedPackages;
            if (StringsKt.startsWith$default(it2, "com.bluestacks.", false, 2, (Object) null)) {
                destination$iv$iv2.add(element$iv$iv);
            }
            pm = pm2;
            prefixes = prefixes2;
            installedPackages = installedPackages2;
        }
        List serviceMatches = (List) destination$iv$iv2;
        String strJoinToString$default = CollectionsKt.joinToString$default(serviceMatches, ", ", null, null, 0, null, null, 62, null);
        if (strJoinToString$default.length() == 0) {
            strJoinToString$default = "<none>";
        }
        String serviceDisplay = strJoinToString$default;
        String serviceMatchValue = String.valueOf(!serviceMatches.isEmpty());
        results.add(new QueryResult("RunningServicePrefix:com.bluestacks.", serviceMatchValue, serviceDisplay));
        return results;
    }

    private final List<QueryResult> buildPrefixResults(String label, List<String> prefixes, List<String> packages) {
        Iterable $this$map$iv;
        List<String> $this$map$iv2 = prefixes;
        int $i$f$map = 0;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv2, 10));
        Iterable $this$mapTo$iv$iv = $this$map$iv2;
        int $i$f$mapTo = 0;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String prefix = (String) item$iv$iv;
            List<String> $this$filter$iv = packages;
            Collection destination$iv$iv2 = new ArrayList();
            Iterator it = $this$filter$iv.iterator();
            while (true) {
                $this$map$iv = $this$map$iv2;
                if (!it.hasNext()) {
                    break;
                }
                Object element$iv$iv = it.next();
                String it2 = (String) element$iv$iv;
                int $i$f$map2 = $i$f$map;
                Iterable $this$mapTo$iv$iv2 = $this$mapTo$iv$iv;
                int $i$f$mapTo2 = $i$f$mapTo;
                if (StringsKt.startsWith$default(it2, prefix, false, 2, (Object) null)) {
                    destination$iv$iv2.add(element$iv$iv);
                }
                $this$map$iv2 = $this$map$iv;
                $i$f$map = $i$f$map2;
                $this$mapTo$iv$iv = $this$mapTo$iv$iv2;
                $i$f$mapTo = $i$f$mapTo2;
            }
            int $i$f$map3 = $i$f$map;
            Iterable $this$mapTo$iv$iv3 = $this$mapTo$iv$iv;
            int $i$f$mapTo3 = $i$f$mapTo;
            List matches = (List) destination$iv$iv2;
            String strJoinToString$default = CollectionsKt.joinToString$default(matches, ", ", null, null, 0, null, null, 62, null);
            int $i$f$mapTo4 = strJoinToString$default.length() == 0 ? 1 : 0;
            if ($i$f$mapTo4 != 0) {
                strJoinToString$default = "<none>";
            }
            String display = strJoinToString$default;
            String matched = String.valueOf(!matches.isEmpty());
            destination$iv$iv.add(new QueryResult(label + ":" + prefix, matched, display));
            $this$map$iv2 = $this$map$iv;
            $i$f$map = $i$f$map3;
            $this$mapTo$iv$iv = $this$mapTo$iv$iv3;
            $i$f$mapTo = $i$f$mapTo3;
        }
        return (List) destination$iv$iv;
    }

    private final List<String> queryLauncherPackages(PackageManager pm) {
        Intent intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        Intrinsics.checkNotNullExpressionValue(intent, "addCategory(...)");
        Iterable activities = Build.VERSION.SDK_INT >= 33 ? pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L)) : pm.queryIntentActivities(intent, 0);
        Intrinsics.checkNotNull(activities);
        Iterable $this$mapNotNull$iv = activities;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            ResolveInfo it = (ResolveInfo) element$iv$iv$iv;
            ActivityInfo activityInfo = it.activityInfo;
            String str = activityInfo != null ? activityInfo.packageName : null;
            if (str != null) {
                destination$iv$iv.add(str);
            }
        }
        return CollectionsKt.distinct((List) destination$iv$iv);
    }

    private final List<String> queryRunningServices() throws SecurityException {
        Object systemService = this.context.getSystemService("activity");
        ActivityManager activityManager = systemService instanceof ActivityManager ? (ActivityManager) systemService : null;
        if (activityManager == null) {
            return CollectionsKt.emptyList();
        }
        Iterable services = activityManager.getRunningServices(20);
        Intrinsics.checkNotNull(services);
        Iterable $this$map$iv = services;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            ActivityManager.RunningServiceInfo it = (ActivityManager.RunningServiceInfo) item$iv$iv;
            destination$iv$iv.add(it.service.getPackageName());
        }
        return CollectionsKt.distinct((List) destination$iv$iv);
    }

    private final boolean isPackageInstalled(PackageManager pm, String packageName) throws PackageManager.NameNotFoundException {
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L));
            } else {
                pm.getPackageInfo(packageName, 0);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private final List<PackageInfo> getInstalledPackages(PackageManager pm) {
        if (Build.VERSION.SDK_INT >= 33) {
            List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L));
            Intrinsics.checkNotNull(installedPackages);
            return installedPackages;
        }
        List<PackageInfo> installedPackages2 = pm.getInstalledPackages(0);
        Intrinsics.checkNotNull(installedPackages2);
        return installedPackages2;
    }

    private final List<String> emulatorPackagePrefixes() {
        return CollectionsKt.listOf((Object[]) new String[]{"com.vphone.", "com.bignox.", "com.nox.mopen.app", "me.haima.", "com.bluestacks", "cn.itools.", "com.kop.", "com.kaopu.", "com.microvirt.", "com.bignox.app"});
    }

    private final List<String> emulatorPackageExact() {
        return CollectionsKt.listOf((Object[]) new String[]{"com.google.android.launcher.layouts.genymotion", "com.nox.mopen.app", "com.bignox.app", "com.microvirt"});
    }

    private final List<QueryResult> queryOpenGlProperties() {
        EGLDisplay display = EGL14.eglGetDisplay(0);
        if (Intrinsics.areEqual(display, EGL14.EGL_NO_DISPLAY)) {
            return openGlUnavailableResults();
        }
        int[] eglVersion = new int[2];
        if (!EGL14.eglInitialize(display, eglVersion, 0, eglVersion, 1)) {
            EGL14.eglTerminate(display);
            return openGlUnavailableResults();
        }
        int[] configAttribs = {12352, 4, 12339, 1, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12344};
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(display, configAttribs, 0, configs, 0, configs.length, numConfigs, 0)) {
            EGL14.eglTerminate(display);
            return openGlUnavailableResults();
        }
        int[] contextAttribs = {12440, 2, 12344};
        EGLContext context = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT, contextAttribs, 0);
        if (context == null || Intrinsics.areEqual(context, EGL14.EGL_NO_CONTEXT)) {
            EGL14.eglTerminate(display);
            return openGlUnavailableResults();
        }
        int[] surfaceAttribs = {12375, 1, 12374, 1, 12344};
        EGLSurface surface = EGL14.eglCreatePbufferSurface(display, configs[0], surfaceAttribs, 0);
        if (surface == null || Intrinsics.areEqual(surface, EGL14.EGL_NO_SURFACE)) {
            EGL14.eglDestroyContext(display, context);
            EGL14.eglTerminate(display);
            return openGlUnavailableResults();
        }
        if (!EGL14.eglMakeCurrent(display, surface, surface, context)) {
            EGL14.eglDestroySurface(display, surface);
            EGL14.eglDestroyContext(display, context);
            EGL14.eglTerminate(display);
            return openGlUnavailableResults();
        }
        String renderer = GLES20.glGetString(7937);
        String vendor = GLES20.glGetString(7936);
        String version = GLES20.glGetString(7938);
        EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroySurface(display, surface);
        EGL14.eglDestroyContext(display, context);
        EGL14.eglTerminate(display);
        QueryResult[] queryResultArr = new QueryResult[3];
        queryResultArr[0] = new QueryResult("OpenGL.Renderer", renderer, renderer == null ? "<unavailable>" : renderer);
        queryResultArr[1] = new QueryResult("OpenGL.Vendor", vendor, vendor == null ? "<unavailable>" : vendor);
        queryResultArr[2] = new QueryResult("OpenGL.Version", version, version != null ? version : "<unavailable>");
        return CollectionsKt.listOf((Object[]) queryResultArr);
    }

    private final List<QueryResult> openGlUnavailableResults() {
        return CollectionsKt.listOf((Object[]) new QueryResult[]{new QueryResult("OpenGL.Renderer", null, "<unavailable>"), new QueryResult("OpenGL.Vendor", null, "<unavailable>"), new QueryResult("OpenGL.Version", null, "<unavailable>")});
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x00f4  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0126  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x015d  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x01aa  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x01de  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0218  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0244  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x027a  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x029a  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x02b8  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x02df  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x02f8  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0304  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final java.util.List<java.lang.String> buildIndicators(java.util.List<org.owasp.mastestapp.MastgTest.QueryResult> r38) {
        /*
            Method dump skipped, instructions count: 795
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.owasp.mastestapp.MastgTest.buildIndicators(java.util.List):java.util.List");
    }

    private final List<String> telephonyIndicators(List<QueryResult> telephonyQueries) {
        List indicators = new ArrayList();
        String line1Number = findValue(telephonyQueries, "TelephonyManager.getLine1Number");
        Set lineNumberMatches = SetsKt.setOf((Object[]) new String[]{"15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564", "15555215566", "15555215568", "15555215570", "15555215572", "15555215574", "15555215576", "15555215578", "15555215580", "15555215582", "15555215584"});
        if ((line1Number.length() > 0) && lineNumberMatches.contains(line1Number)) {
            indicators.add("TelephonyManager.getLine1Number=" + line1Number);
        }
        String networkOperatorName = findValue(telephonyQueries, "TelephonyManager.getNetworkOperatorName");
        if ((networkOperatorName.length() > 0) && StringsKt.contains$default((CharSequence) networkOperatorName, (CharSequence) "android", false, 2, (Object) null)) {
            indicators.add("TelephonyManager.getNetworkOperatorName=" + networkOperatorName);
        }
        String voiceMailNumber = findValue(telephonyQueries, "TelephonyManager.getVoiceMailNumber");
        if ((voiceMailNumber.length() > 0) && Intrinsics.areEqual(voiceMailNumber, "15552175049")) {
            indicators.add("TelephonyManager.getVoiceMailNumber=" + voiceMailNumber);
        }
        return indicators;
    }

    private final List<String> packageIndicators(List<QueryResult> packageQueries) {
        List<QueryResult> $this$filter$iv = packageQueries;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            QueryResult it = (QueryResult) element$iv$iv;
            boolean z = false;
            if (Intrinsics.areEqual(it.getRawValue(), "true") && (StringsKt.startsWith$default(it.getName(), "InstalledPackagePrefix:", false, 2, (Object) null) || StringsKt.startsWith$default(it.getName(), "LauncherPackagePrefix:", false, 2, (Object) null) || StringsKt.startsWith$default(it.getName(), "PackageManager.hasPackage:", false, 2, (Object) null) || StringsKt.startsWith$default(it.getName(), "RunningServicePrefix:", false, 2, (Object) null))) {
                z = true;
            }
            if (z) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        Iterable $this$map$iv = (List) destination$iv$iv;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            QueryResult it2 = (QueryResult) item$iv$iv;
            destination$iv$iv2.add(it2.getName() + "=" + it2.getDisplayValue());
        }
        return (List) destination$iv$iv2;
    }

    private final List<String> openGlIndicators(List<QueryResult> openGlQueries) {
        String rendererValue = findDisplayValue(openGlQueries, "OpenGL.Renderer");
        String rendererMatch = findValue(openGlQueries, "OpenGL.Renderer");
        if (StringsKt.contains$default((CharSequence) rendererMatch, (CharSequence) "bluestacks", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) rendererMatch, (CharSequence) "translator", false, 2, (Object) null)) {
            return CollectionsKt.listOf("OpenGL.Renderer=" + rendererValue);
        }
        return CollectionsKt.emptyList();
    }

    private final boolean containsAny(String value, List<String> tokens) {
        List<String> $this$any$iv = tokens;
        if (($this$any$iv instanceof Collection) && $this$any$iv.isEmpty()) {
            return false;
        }
        for (Object element$iv : $this$any$iv) {
            String it = (String) element$iv;
            if (StringsKt.contains$default((CharSequence) value, (CharSequence) it, false, 2, (Object) null)) {
                return true;
            }
        }
        return false;
    }

    private final String safeBuildSerial() {
        try {
            return Build.getSerial();
        } catch (Exception e) {
            return Build.SERIAL;
        }
    }

    private final String findValue(List<QueryResult> list, String name) {
        Object element$iv;
        String rawValue;
        List<QueryResult> $this$firstOrNull$iv = list;
        Iterator it = $this$firstOrNull$iv.iterator();
        while (true) {
            if (it.hasNext()) {
                element$iv = it.next();
                QueryResult it2 = (QueryResult) element$iv;
                if (Intrinsics.areEqual(it2.getName(), name)) {
                    break;
                }
            } else {
                element$iv = null;
                break;
            }
        }
        QueryResult queryResult = (QueryResult) element$iv;
        if (queryResult != null && (rawValue = queryResult.getRawValue()) != null) {
            String lowerCase = rawValue.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
            if (lowerCase != null) {
                return lowerCase;
            }
        }
        return "";
    }

    private final String findDisplayValue(List<QueryResult> list, String name) {
        Object element$iv;
        String displayValue;
        List<QueryResult> $this$firstOrNull$iv = list;
        Iterator it = $this$firstOrNull$iv.iterator();
        while (true) {
            if (it.hasNext()) {
                element$iv = it.next();
                QueryResult it2 = (QueryResult) element$iv;
                if (Intrinsics.areEqual(it2.getName(), name)) {
                    break;
                }
            } else {
                element$iv = null;
                break;
            }
        }
        QueryResult queryResult = (QueryResult) element$iv;
        return (queryResult == null || (displayValue = queryResult.getDisplayValue()) == null) ? "" : displayValue;
    }

    private final List<String> ensureTelephonyPermissions() {
        List permissions = CollectionsKt.mutableListOf("android.permission.READ_PHONE_STATE");
        permissions.add("android.permission.READ_PHONE_NUMBERS");
        List $this$filter$iv = permissions;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String it = (String) element$iv$iv;
            String it2 = ContextCompat.checkSelfPermission(this.context, it) != 0 ? 1 : null;
            if (it2 != null) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        List missing = (List) destination$iv$iv;
        if (missing.isEmpty()) {
            return CollectionsKt.emptyList();
        }
        Context context = this.context;
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity == null) {
            return missing;
        }
        List $this$toTypedArray$iv = missing;
        ActivityCompat.requestPermissions(activity, (String[]) $this$toTypedArray$iv.toArray(new String[0]), PointerIconCompat.TYPE_CONTEXT_MENU);
        return missing;
    }

    private final String telephonyPermissionNote(List<String> missingPermissions) {
        if (missingPermissions.isEmpty()) {
            return "";
        }
        List<String> $this$map$iv = missingPermissions;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            String it = (String) item$iv$iv;
            destination$iv$iv.add(StringsKt.substringAfterLast$default(it, '.', (String) null, 2, (Object) null));
        }
        List displayNames = (List) destination$iv$iv;
        return "\n\nGrant " + CollectionsKt.joinToString$default(displayNames, ", ", null, null, 0, null, null, 62, null) + " and re-run to read telephony identifiers.";
    }
}
