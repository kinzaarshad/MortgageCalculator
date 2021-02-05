package com.example.mortgagecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.Math.pow;

public class MainActivity extends AppCompatActivity {
    String price;
    String deposit;

    @BindView(R.id.priceField)
    EditText priceField;

    @BindView(R.id.depositField)
    EditText depositField;

    @BindView(R.id.AnnualInterest)
    EditText AnnualInterest;

    @BindView(R.id.repaymentPeriod)
    EditText repaymentPeriod;

    @BindView(R.id.monthlyRepayment)
    TextView monthlyRepayment;

    @BindView(R.id.progressBar)
    ProgressBar depositPercentage;

    @BindView(R.id.txtProgress)
    TextView progressPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Inflate the layout for this fragment
        price="10000";
        deposit=calculateDeposit(price)+"";
        setValues(price,deposit,"2.5","25");
        priceField.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String p= priceField.getText().toString();
                price=p.substring(1);
                if(p.length()==0){
                    priceField.setText("$");
                }
                if(p.length()<3){
                    Toast.makeText(getApplicationContext(),"Property price is too less",Toast.LENGTH_LONG).show();
                }else{
                    deposit=calculateDeposit(p.substring(1))+"";
                    depositField.setText("$"+deposit);
                    Double monthlyAmmount=calculate(Double.parseDouble(price),
                            Double.parseDouble(deposit),
                            Double.parseDouble(AnnualInterest.getText().toString()),
                            0.0,
                            (int) Double.parseDouble(repaymentPeriod.getText().toString()));
                    monthlyRepayment.setText("$"+monthlyAmmount);
                }


            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        depositField.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String d= depositField.getText().toString();
                if(d.length()<2){
                    depositField.setText("$0");
                    d="$1";
                }

                deposit=d.substring(1);
                int p=(int) calculatePercentage(price,deposit);
                depositPercentage.setProgress(p);
                progressPercentage.setText(p+"%");
                Double monthlyAmmount=calculate(Double.parseDouble(price),
                        Double.parseDouble(d.substring(1)),
                        Double.parseDouble(AnnualInterest.getText().toString()),
                        0.0,
                        (int) Double.parseDouble(repaymentPeriod.getText().toString()));
                monthlyRepayment.setText("$"+monthlyAmmount);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        AnnualInterest.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String a= AnnualInterest.getText().toString();
                if(a.length()==0){
                    AnnualInterest.setText("0");
                    a="0";
                }
                Double monthlyAmmount=calculate(Double.parseDouble(price),
                        Double.parseDouble(deposit),
                        Double.parseDouble(a),
                        0.0,
                        (int) Double.parseDouble(repaymentPeriod.getText().toString()));
                monthlyRepayment.setText("$"+monthlyAmmount);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        repaymentPeriod.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String r= repaymentPeriod.getText().toString();
                if(r.length()==0){
                    repaymentPeriod.setText("0");
                    r="0";
                }
                Double monthlyAmmount=calculate(Double.parseDouble(price),
                        Double.parseDouble(deposit),
                        Double.parseDouble(AnnualInterest.getText().toString()),
                        0.0,
                        (int) Double.parseDouble(r));
                monthlyRepayment.setText("$"+monthlyAmmount);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    public void setValues(String price,String deposit,String annualInterest,String repaymentTime){
        //float monthlyAmmount=calculateMonthlyRepayments(price,deposit,annualInterest,repaymentTime);
        Double monthlyAmmount=calculate(Double.parseDouble(price),
                Double.parseDouble(deposit),
                Double.parseDouble(annualInterest),
                0.0,
                (int) Double.parseDouble(repaymentTime));
        priceField.setText("$"+price);
        depositField.setText("$"+deposit);
        AnnualInterest.setText("2.5");
        repaymentPeriod.setText("25");
        depositPercentage.setProgress(10);
        progressPercentage.setText(10+"%");
        monthlyRepayment.setText("$"+monthlyAmmount);
    }

    float calculateDeposit(String price){
        float floatPrice=Float.parseFloat(price);
        float deposit= (float) ((0.1)*floatPrice);
        return deposit;
    }

    float calculatePercentage(String price,String deposit){
        float floatPrice=Float.parseFloat(price);
        float floatDeposit=Float.parseFloat(deposit);
        float percentage=(floatDeposit/floatPrice)*100;
        return percentage;
    }

    public Double calculate(Double HomeValue, Double DownPayment, Double InterestRate, Double PropertyTaxRate, Integer Terms){
        Double Rate = (InterestRate / 100) / 12;
        Double pv = HomeValue - DownPayment;
        Integer nper = 12 * Terms;
        Double MonthlyPayment = pv * (Rate * pow((1 + Rate), nper)) / (pow((1 + Rate), nper) - 1);
        Double TotalPayment = MonthlyPayment * 12 * Terms;
        Double TotalPropertyTaxPaid = HomeValue * PropertyTaxRate/100 * Terms;
        Double TotalInterestPaid = TotalPayment - pv;
        Double MonthlyPaymentWithProperty = MonthlyPayment + TotalPropertyTaxPaid/Terms/12;
        String mp = "$" + MonthlyPayment;
        String mpp = "$" + MonthlyPaymentWithProperty;
        String ip = "$" + TotalInterestPaid;
        String ptp = "$" + TotalPropertyTaxPaid;

        return Double.valueOf(Math.round(MonthlyPaymentWithProperty));


    }

}