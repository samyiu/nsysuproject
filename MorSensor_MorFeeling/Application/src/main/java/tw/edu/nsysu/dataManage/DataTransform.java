package tw.edu.nsysu.dataManage;

import android.util.Log;
import android.widget.Toast;

import tw.edu.nsysu.morsenser_morfeeling.AlcoholViewActivity;
import tw.edu.nsysu.morsenser_morfeeling.ColorViewActivity;
import tw.edu.nsysu.morsenser_morfeeling.MainActivity;
import tw.edu.nsysu.morsenser_morfeeling.PIRViewActivity;

/**
 * Created by 1404011 on 2015/5/4.
 */
public class DataTransform {
    private static final String TAG = "DataTransform";
    static float data[]  = new float[9];
    static short RawData[]  = new short[20];

    public static float[] getData(){
        return data;
    }

    private static void CheckNegative(byte[] value){
        for(int i = 2; i < 20; i++){
            RawData[i] = (short)value[i];
            if(value[i] < 0) { RawData[i] = (short)(value[i] + 256); }
        }
    }

    public static void TransformIMU(byte[] value){
        CheckNegative(value);

        //Gryo: value[2][3] / 32.8
        data[0] = (float)((short) (RawData[2] * 256 + RawData[3]) / 32.8); //Gryo x
        data[1] = (float)((short) (RawData[4] * 256 + RawData[5]) / 32.8); //Gryo y
        data[2] = (float)((short) (RawData[6] * 256 + RawData[7]) / 32.8); //Gryo z

        //Acc: value[8][9] / 4096
        data[3] = (float)((short) (RawData[8] * 256 + RawData[9]) / 4096.0); //Acc x
        data[4] = (float)((short) (RawData[10] * 256 + RawData[11]) / 4096.0); //Acc y
        data[5] = (float)((short) (RawData[12] * 256 + RawData[13]) / 4096.0); //Acc z

        //Mag: value[15][14] / 3.41 / 100 (??:MagZ ???-1)
        data[7] = (float)((short) (RawData[15] * 256 + RawData[14]) / 3.41 / 100); //Mag x
        data[6] = (float)((short) (RawData[17] * 256 + RawData[16]) / 3.41 / 100); //Mag y
        data[8] = (float)((short) (RawData[19] * 256 + RawData[18]) / 3.41 / -100); //Mag z


        //IMUViewPlusActivity.DisplayIMUData();
    }

    public static void TransformTempHumi(byte[] value){

        CheckNegative(value);

        float Temp_data = (float) (RawData[2] * 256 + RawData[3]);
        float RH_data = (float) (RawData[4] * 256 + RawData[5]);

        data[1] = (float) (Temp_data * 175.72 / 65536.0 - 46.85); //Temp
        data[2] = (float) (RH_data * 125.0 / 65536.0 - 6.0); //RH

        //THU.haha();
    }



    public static void TransformUV(byte[] value){
        CheckNegative(value);

        float UV_data = (float) ( RawData[3] * 256 +  RawData[2]);

        data[0] = (float) (UV_data / 100.0); //UV

        //THU.haha();
    }

    public static float RedCalibration = 1;
    public static float GreenCalibration = 1;
    public static float BlueCalibration = 1;

    public static String Red = "FF";
    public static String Green = "FF";
    public static String Blue = "FF";
    public static void TransformColor(byte[] value) {
        CheckNegative(value);

        data[0] = RawData[3];
        data[1] = RawData[5];
        data[2] = RawData[7];

        if(ColorViewActivity.Calibration){
            RedCalibration = 255f / data[0];
            GreenCalibration = 255f / data[1];
            BlueCalibration = 255f / data[2];

            ColorViewActivity.Calibration = false;
        }
//        Log.e(TAG,"RedCalibration:"+RedCalibration+" GreenCalibration:"+GreenCalibration+" BlueCalibration:"+BlueCalibration);

        data[0] *= RedCalibration;
        data[1] *= GreenCalibration;
        data[2] *= BlueCalibration;
        if((short)data[0] * RedCalibration > 255){ data[0] = 255; }
        if((short)data[1] * GreenCalibration > 255){ data[1] = 255; }
        if((short)data[2] * BlueCalibration > 255){ data[2] = 255; }
//        Log.e(TAG,"Calibration data[0]:"+data[0]+" data[1]:"+data[1]+" data[2]:"+data[2]);


        Red = Integer.toHexString((int)data[0] & 0xFF);
        Green = Integer.toHexString((int)data[1] & 0xFF);
        Blue = Integer.toHexString((int)data[2] & 0xFF);

        if(Red.length()==1){ Red = "0" + Red;}
        if(Green.length()==1){ Green = "0" + Green;}
        if(Blue.length()==1){ Blue = "0" + Blue;}

//        Log.e(TAG, "Red:" + Red + " Green:" + Green + " Blue:" + Blue);
        ColorViewActivity.DisplayColorData();
    }

    public static void TransformPIR(byte[] value) {
        CheckNegative(value);

        // <10cm ALC_out=3.3 ; >10cm ALC_out=0
        data[0] = (float) ((RawData[2] * 256.0 + RawData[3]) / 4096.0 * 3.3);

        PIRViewActivity.DisplayPIRData();
    }

    static float a, b, a_p, ReadConter = 0;
    public static void TransformAlcohol(byte[] value){
        ReadConter++;
        CheckNegative(value);

        float Alc_out;
        Alc_out = (float) (( RawData[2] * 256.0 + RawData[3]) / 4096.0 * 3.3);

        //歸零校正
        if(ReadConter == 1){
            a = 1f / (1.8f - Alc_out);
            b = 1.8f * (1f -a);
            a_p = a * Alc_out + b;
            data[2] = a;
            data[3] = b;
            data[4] = a_p;
        }

        //Check Voltage
        data[1] = Alc_out;
//            Alc_out /= 8;
//            ALC_out = 3.3 * raw_data / 4096

        if (Alc_out >= 1.8) { //y = -2.9427*Alc_out^2 + 15.225*Alc_out - 17.551
            data[0] = -2.9427f * Alc_out * Alc_out + 15.225f * Alc_out - 17.551f;
        } else { //y = 0.1586*Alc_out^2 - 0.1199*Alc_out
            data[0] = 0.1586f * a_p * a_p - 0.1199f * a_p;
        }

        AlcoholViewActivity.DisplayAlcoholData();
    }

    // Byte[] to HexString
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte byteChar : bytes) {
            sb.append(String.format("%02X", byteChar));
        }
        return sb.toString();
    }

    // HexString to Byte[]
    public static byte[] hexToBytes(String hexString) {
        char[] hex = hexString.toCharArray();
        //轉rawData長度減半
        int length = hex.length / 2;
        byte[] rawData = new byte[length];
        for (int i = 0; i < length; i++) {
            //先將hex資料轉10進位數值
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            //將第一個值的二進位值左平移4位,ex: 00001000 => 10000000 (8=>128)
            //然後與第二個值的二進位值作聯集ex: 10000000 | 00001100 => 10001100 (137)
            int value = (high << 4) | low;
            //與FFFFFFFF作補集
            if (value > 127)
                value -= 256;
            //最後轉回byte就OK
            rawData[i] = (byte) value;
        }
        return rawData;
    }

    static int count = 0;
    static int[] LED = new int[2];
    static float IR = 0, RED = 0, SpO2Data = 0, HeartRateData = 0;
    static float Red_DC = 0, Red_AC = 0, IR_DC = 0, IR_AC = 0;
    static float Red_DC_Raw = 0, Red_AC_Raw_Max = 0, Red_AC_Raw_Min = 0;
    static float IR_DC_Raw = 0, IR_AC_Raw_Max = 0, IR_AC_Raw_Min = 0;
    static double[] RawIRdata = new double[1024];
    public static void TransformSpO2(String value) {
//        if(value.equals("FBA007D000000000000000000000000000000000")){ //1000
        if(value.equals("F1A0080000000000000000000000000000000000")){ //1024
            Log.i(TAG,"Initial");
            count = 0;
            IR = 0; RED = 0; SpO2Data = 0; HeartRateData = 0;
            Red_DC = 0; Red_AC = 0; IR_DC = 0; IR_AC = 0;
            Red_DC_Raw = 0; Red_AC_Raw_Max = 0; Red_AC_Raw_Min = 0; IR_DC_Raw = 0; IR_AC_Raw_Max = 0; IR_AC_Raw_Min = 0;

            for (int i = 0; i<1024;i++){ RawIRdata[i] = 0; }
            return;
        }

        if(value.substring(8,36).equals("00000000000000000000000000000000")){
            if(count<3)
                Toast.makeText(MainActivity.mMainActivity, "MorSensor not find the SD card.", Toast.LENGTH_SHORT).show();
            return;
        }

        LED = SpO2Transform.RawDataToData(SpO2Transform.ASCIItoHex(value, 8));

        count++;

        if(count%2 == 0){ //LED1 = IR
            data[2] = SpO2Transform.FixedToFloatLED(LED[0]); //IR
            data[3] = SpO2Transform.FixedToFloatLED(LED[1]); //IR A

            IR_DC_Raw += data[2];
            if(count==2){
                IR_AC_Raw_Max = data[2];
                IR_AC_Raw_Min = data[2];
            }

            if(IR_AC_Raw_Max < data[2]) { IR_AC_Raw_Max = data[2]; }
            if(IR_AC_Raw_Min > data[2]) { IR_AC_Raw_Min = data[2]; }

            // 均方根、四捨五入小數第五位( round(sqrt(value/count) * x) / x)
            // round:四捨五入 sqrt:開根號 count:資料數 x:小數第幾位(10的倍數)
            Red_AC = SpO2Transform.FixedToFloatAC(Red_AC_Raw_Max, Red_AC_Raw_Min);
            Red_DC = SpO2Transform.FixedToFloatDC(Red_DC_Raw, (count / 2 + 1));
            IR_AC = SpO2Transform.FixedToFloatAC(IR_AC_Raw_Max, IR_AC_Raw_Min);
            IR_DC = SpO2Transform.FixedToFloatDC(IR_DC_Raw, (count / 2 + 1));

            IR = IR_AC/IR_DC;
            RED = Red_AC/Red_DC;

            RawIRdata[count / 2 - 1 ] = data[2]; //IR
            if(data[2]==0){
                RawIRdata[count / 2 - 1 ] = 0;
            }

            data[4] = SpO2Data = (int)(SpO2Transform.FixedToFloatSpO2(RED, IR) * 100) / 100f;
//            data[4] = SpO2Data = (int)(SpO2Transform.FixedToFloatSpO2(IR, RED) * 100) / 100f;
            data[5] = HeartRateData = 0; //Heart Rate
            data[6] = count / 2;

            if(data[6] == 1024){
                data[5] = HeartRateData = (int)(SpO2Transform.FixedToFloatHeartRate(RawIRdata) * 10) / 10f;
                Log.d(TAG, "count:"+data[6]+" SpO2Data:" + SpO2Data + " HeartRateData:"+HeartRateData + " IR:"+IR+" RED:"+RED);
            }



        }else{ //LED2 = RED
            data[0] = (float)LED[0] * 1.2f / (float)Math.pow(2,21); //RED
            data[1] = (float)LED[1] * 1.2f / (float)Math.pow(2,21); //RED A

            Red_DC_Raw += data[0];
            if(count==1){
                Red_AC_Raw_Max = data[0];
                Red_AC_Raw_Min = data[0];
            }

            if(Red_AC_Raw_Max < data[0]) { Red_AC_Raw_Max = data[0]; }
            if(Red_AC_Raw_Min > data[0]) { Red_AC_Raw_Min = data[0]; }
        }
    }
}
