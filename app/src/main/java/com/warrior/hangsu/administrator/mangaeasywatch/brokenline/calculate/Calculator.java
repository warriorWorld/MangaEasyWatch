package com.warrior.hangsu.administrator.mangaeasywatch.brokenline.calculate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.warrior.hangsu.administrator.mangaeasywatch.R;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator extends Dialog implements
		View.OnClickListener {

	private ToggleButton cut_button;
	private View science_layout, easy_layout;
	private Button lBracket;
	private Button rBracket;
	private Button pai;
	private Button delete;
	private Button c;
	private Button e;
	private Button ln;
	private Button log;
	private Button jiecheng;
	private Button kaifang;
	private Button genhao;
	private Button sin;
	private Button number7;
	private Button number8;
	private Button number9;
	private Button minus;
	private Button cos;
	private Button number4;
	private Button number5;
	private Button number6;
	private Button Multiply;
	private Button plus;
	private Button tan;
	private Button number1;
	private Button number2;
	private Button number3;
	private Button devide;
	private Button cot;
	private Button number0;
	private Button del;
	private Button ans;
	private Button equal;
	private EditText equaltion;
	private EditText result;
	private String tempStr = "";
	private final double p = 3.141592654;
	private final double ze = 2.718281828459;
	private String res = "";
	private float[] XList;
	private ArrayList<PointF> XYList = new ArrayList<PointF>();
	private Context context;
	private OnCalculatorResultListener onCalculatorResultListener;
	private ArrayList<Integer> specialPoint = new ArrayList<Integer>();
	// private int[] specialPoint = { Integer.MAX_VALUE };
	private int xPosition;

	public Calculator(Context context, float[] XList) {
		super(context);
		this.context = context;
		this.XList = XList;
	}

	/**
	 * ans不需要,result也不需要了(result现在是个幽灵,不显示但是却在行动),支持y=nx+m形式,需要一个Xlist(X)
	 * 根据该list计算出XYList(X,Y) 并通知view绘制出曲线
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_calculator);
		equaltion = (EditText) findViewById(R.id.equation);
		result = (EditText) findViewById(R.id.result);
		initUI();
		changeMode(true); // 初始化两种计算器(简易/科学)

		Window window = this.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		WindowManager wm = ((Activity) context).getWindowManager();
		Display d = wm.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.75); // 高度设置为屏幕的0.6
		lp.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
		// window.setGravity(Gravity.LEFT | Gravity.TOP);
		window.setGravity(Gravity.CENTER);
		// lp.x = 100; // 新位置X坐标
		// lp.y = 100; // 新位置Y坐标
		// lp.height = 30;
		// lp.width = 20;
		window.setAttributes(lp);
	}

	private void initUI() {
		easy_layout = findViewById(R.id.easy_layout);
		science_layout = findViewById(R.id.science_layout);
		cut_button = (ToggleButton) findViewById(R.id.cut);

		cut_button.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				changeMode(isChecked);
			}
		});
	}

	private void changeMode(boolean isEasy) {
		if (isEasy) {
			easy_layout.setVisibility(View.VISIBLE);
			science_layout.setVisibility(View.GONE);
			initLayoutUI(easy_layout);
		} else {
			easy_layout.setVisibility(View.GONE);
			science_layout.setVisibility(View.VISIBLE);
			initLayoutUI(science_layout);
		}
	}

	private void initLayoutUI(View v) {
		lBracket = (Button) v.findViewById(R.id.l_bracket);
		rBracket = (Button) v.findViewById(R.id.r_bracket);
		pai = (Button) v.findViewById(R.id.pai);
		delete = (Button) v.findViewById(R.id.delete);
		c = (Button) v.findViewById(R.id.c);
		e = (Button) v.findViewById(R.id.e);
		ln = (Button) v.findViewById(R.id.ln);
		log = (Button) v.findViewById(R.id.log);
		jiecheng = (Button) v.findViewById(R.id.jiecheng);
		kaifang = (Button) v.findViewById(R.id.kaifang);
		genhao = (Button) v.findViewById(R.id.genhao);
		sin = (Button) v.findViewById(R.id.sin);
		number7 = (Button) v.findViewById(R.id.number_7);
		number8 = (Button) v.findViewById(R.id.number_8);
		number9 = (Button) v.findViewById(R.id.number_9);
		minus = (Button) v.findViewById(R.id.minus);
		cos = (Button) v.findViewById(R.id.cos);
		number4 = (Button) v.findViewById(R.id.number_4);
		number5 = (Button) v.findViewById(R.id.number_5);
		number6 = (Button) v.findViewById(R.id.number_6);
		Multiply = (Button) v.findViewById(R.id.Multiply);
		plus = (Button) v.findViewById(R.id.plus);
		tan = (Button) v.findViewById(R.id.tan);
		number1 = (Button) v.findViewById(R.id.number_1);
		number2 = (Button) v.findViewById(R.id.number_2);
		number3 = (Button) v.findViewById(R.id.number_3);
		devide = (Button) v.findViewById(R.id.devide);
		cot = (Button) v.findViewById(R.id.cot);
		number0 = (Button) v.findViewById(R.id.number_0);
		del = (Button) v.findViewById(R.id.del);
		ans = (Button) v.findViewById(R.id.ans);
		equal = (Button) v.findViewById(R.id.equal);
		if (v == science_layout) {
			lBracket.setOnClickListener(this);
			rBracket.setOnClickListener(this);
			pai.setOnClickListener(this);
			e.setOnClickListener(this);
			ln.setOnClickListener(this);
			log.setOnClickListener(this);
			jiecheng.setOnClickListener(this);
			kaifang.setOnClickListener(this);
			genhao.setOnClickListener(this);
			sin.setOnClickListener(this);
			cos.setOnClickListener(this);
			tan.setOnClickListener(this);
			cot.setOnClickListener(this);
		}

		delete.setOnClickListener(this);
		c.setOnClickListener(this);
		number7.setOnClickListener(this);
		number8.setOnClickListener(this);
		number9.setOnClickListener(this);
		minus.setOnClickListener(this);
		number4.setOnClickListener(this);
		number5.setOnClickListener(this);
		number6.setOnClickListener(this);
		Multiply.setOnClickListener(this);
		plus.setOnClickListener(this);
		number1.setOnClickListener(this);
		number2.setOnClickListener(this);
		number3.setOnClickListener(this);
		devide.setOnClickListener(this);
		number0.setOnClickListener(this);
		del.setOnClickListener(this);
		ans.setOnClickListener(this);
		equal.setOnClickListener(this);

	}

	private void setText(View v) {
		tempStr = tempStr + ((Button) v).getText().toString();
		equaltion.setText(tempStr);
		equaltion.setSelection(equaltion.getText().length());
		// 只有加减乘除的时候才会出现ans
		// if (!TextUtils.isEmpty(result.getText())
		// && verifyOperator(((Button) v).getText().toString())) {
		// equaltion.setText("Ans" + ((Button) v).getText());
		// result.setText("");
		// tempStr = "Ans" + ((Button) v).getText().toString();
		// } else if (!TextUtils.isEmpty(result.getText())) {
		// equaltion.setText("" + ((Button) v).getText());
		// result.setText("");
		// tempStr = "" + ((Button) v).getText().toString();
		// }
	}

	private void getresult(String exp, int position)
			throws NumberFormatException, IndexOutOfBoundsException,
			MyException {

		exp = exp.replaceAll("log", "l").replaceAll("ln", "n")
				.replaceAll("sin", "s").replaceAll("cos", "c")
				.replaceAll("tan", "t").replaceAll("cot", "o")
				.replaceAll("Ans", "a");

		char[] expList = exp.toCharArray();
		StringBuilder expsb = new StringBuilder();
		for (int i = 0; i < expList.length; i++) {
			char e = expList[i];
			// 为防止越界而做的判断
			char lastChar = i == 0 ? 'z' : expList[i - 1];
			char nextChar = i == expList.length - 1 ? 'y' : expList[i + 1];
			if (i == 0 && verifyExp(expList[0], lastChar)) {
				expsb.append(e);
				expsb.append(',');
			} else if (e == 'x' && verifyNumber(String.valueOf(lastChar))
					&& !verifyLetter(lastChar)) {
				expsb.append(',');
				expsb.append('*');
				expsb.append(',');
				expsb.append('x');
				expsb.append(',');

			} else if (e == '-' && nextChar == '('
					&& (lastChar == '+' || lastChar == '(')) {
				expsb.append('0');
				expsb.append(',');
				expsb.append('-');
				expsb.append(',');

			} else if (e == '-' && nextChar == '(' && (lastChar == '-')) {
				expsb.delete(expsb.length() - 3, expsb.length() - 1);
				expsb.append(',');
				expsb.append('+');
				expsb.append(',');

			} else if (e == '-' && nextChar == '(' && (lastChar == '*')) {
				expsb.append("-1");
				expsb.append(',');
				expsb.append('*');
				expsb.append(',');

			} else if (e == '-' && nextChar == '(' && (lastChar == '/')) {
				expsb.append("-1");
				expsb.append(',');
				expsb.append('/');
				expsb.append(',');

			} else if (e == '-' && nextChar == '(' && (lastChar == 's')) {
				throw new MyException("- before sin");

			} else if (e == '-' && nextChar == '(' && (lastChar == 'c')) {
				throw new MyException("- before cos");

			} else if (e == '-' && nextChar == '(' && (lastChar == 't')) {
				throw new MyException("- before tan");

			} else if (e == '-' && nextChar == '(' && (lastChar == 'o')) {
				throw new MyException("- before cot");

			} else if (verifyExp(e, lastChar)) {
				expsb.append(',');
				expsb.append(e);
				expsb.append(',');
			} else {
				expsb.append(e);
			}

		}

		exp = expsb.toString();

		exp = exp.replaceAll(",,", ",");
		String[] strList = exp.split(",");
		List<String> list = new ArrayList<String>(Arrays.asList(strList));
		for (int i = 0; i < strList.length; i++) {
			Log.d("sss", "for strList is:" + strList[i]);
		}

		while (list.contains("(") && list.contains(")")) {
			// 括号方法
			List<String> bracketList = new ArrayList<String>();
			int end = IndexOf(list, ")", 0);
			int start = lastIndexOf(list, "(", end);
			int f = end - start;
			for (int j = start + 1; j < f + start; j++) {
				bracketList.add(list.get(j));
			}
			// for (int z = 0; z < bracketList.size(); z++) {
			// Logger.d("bracketList is" + bracketList.get(z));
			// }
			calculator(bracketList, String.valueOf(XList[position]));
			String bracketListResult = result.getText().toString();
			for (int i = start; i < f + start; i++) {
				list.remove(start);
				list.set(start, bracketListResult);
			}

		}
		calculator(list, String.valueOf(XList[position]));
	}

	private int lastIndexOf(List<String> list, String str, int start) {
		int location = -1;
		for (int i = start; i >= 0; i--) {
			String s = list.get(i);
			if (s.equals(str)) {
				location = i;
				break;
			}
		}
		return location;
	}

	private int IndexOf(List<String> list, String str, int start) {
		int location = -1;
		for (int i = start; i < list.size(); i++) {
			String s = list.get(i);
			if (s.equals(str)) {
				location = i;
				break;
			}
		}
		return location;
	}

	private boolean verifyExp(char e, char lastChar) {
		return e == '+'
				|| (e == '-' && 'z' != lastChar && lastChar != '*'
						&& lastChar != '/' && lastChar != '+'
						&& lastChar != '-' && lastChar != '('
						&& lastChar != '^' && lastChar != 's'
						&& lastChar != 'c' && lastChar != 't' && lastChar != 'o')
				|| e == '/' || e == '*' || e == '(' || e == ')' || e == 'π'
				|| e == 'e' || e == 'n' || e == 'l' || e == '^' || e == '!'
				|| e == '√' || e == 's' || e == 'c' || e == 't' || e == 'o'
				|| e == 'a' || e == 'x';
	}

	private boolean verifyLetter(char e) {
		return e == '+' || e == '-' || e == '/' || e == '*' || e == '('
				|| e == ')' || e == 'π' || e == 'e' || e == 'n' || e == 'l'
				|| e == '^' || e == '!' || e == '√' || e == 's' || e == 'c'
				|| e == 't' || e == 'o' || e == 'a' || e == 'x';
	}

	private void calculator(List<String> list, String xValue) {
		for (int i = 0; i < list.size(); i++) {
			String x = list.get(i);
			if (x.equals("π")) {
				list.set(i, String.valueOf(p));
			} else if (x.equals("e")) {
				list.set(i, String.valueOf(ze));
			} else if (x.equals("x")) {
				if (verifyCanToInt(xValue)) {
					float floatX = Float.valueOf(xValue);
					int intX = (int) floatX;
					xValue = String.valueOf(intX);
				}
				list.set(i, xValue);
			} else if (x.equals("a")) {
				if (!TextUtils.isEmpty(res)) {
					list.set(i, res);
				}
			}
		}
		try {
			qys(list, "^", 0);
			qys(list, "!", 0);
			qys(list, "s", 0);
			qys(list, "c", 0);
			qys(list, "t", 0);
			qys(list, "o", 0);
			qys(list, "n", 0);
			qys(list, "l", 0);
			qys(list, "√", 0);
		} catch (MyException e) {
			result.setText("");
			// specialPoint = xPosition;
			specialPoint.add(xPosition);
			Toast.makeText(context, e.getMessage() + specialPoint.size(), 0)
					.show();
		}
		ys(list, "/");
		ys(list, "*");
		ys(list, "-");
		ys(list, "+");
		// 运算完后如果有结果就计入到result�?,如果没有则判断公式是否是纯数�?,若是,则直�?=数字
		if (!TextUtils.isEmpty(result.getText().toString())) {
			res = result.getText().toString();
		} else {
			Pattern p = Pattern.compile("[0-9.-]*");
			Matcher m = p.matcher(equaltion.getText());
			if (m.matches()) {
				if (verifyNumber(equaltion.getText().toString())) {
					result.setText(equaltion.getText());
					res = result.getText().toString();
				}
			}
		}
	}

	// private boolean isContainYsf(List<String> list) {
	// boolean res = false;
	// for (int i = 0; i < list.size(); i++) {
	// String str = list.get(i);
	// if ("+".equals(str) || "-".equals(str) || "*".equals(str)
	// || "/".equals(str) || "^".equals(str)) {
	// res = true;
	// break;
	// }
	// }
	// return res;
	// }

	private void ys(List<String> list, String ysf) {
		String s1 = "";
		String s2 = "";
		double d1 = 0;
		double d2 = 0;
		double r = 0;

		for (int i = 1; i < list.size() - 1; i++) {
			// 避免4*崩溃
			String x = list.get(i);
			if (x.equals(ysf)) {
				s1 = list.get(i - 1);
				d1 = parseDouble(s1);
				s2 = list.get(i + 1);
				d2 = parseDouble(s2);
				if ("*".equals(ysf)) {
					r = d1 * d2;
				} else if ("/".equals(ysf)) {
					r = d1 / d2;
				} else if ("+".equals(ysf)) {
					r = d1 + d2;
				} else if ("-".equals(ysf)) {
					r = d1 - d2;
				}

				list.remove(i - 1);
				list.remove(i - 1);
				list.set(i - 1, String.valueOf(r));
				if (String.valueOf(r).contains(".")
						&& String.valueOf(r)
								.substring(String.valueOf(r).length() - 1)
								.equals("0")
						&& !String.valueOf(r).contains("E")) {
					int r1 = (int) r;
					result.setText(String.valueOf(r1));
					// Logger.d("执行�?" + r1);
				} else {
					BigDecimal b = new BigDecimal(r);
					result.setText(String.valueOf(b.setScale(8,
							BigDecimal.ROUND_HALF_UP).doubleValue()));
				}

				if ("/".equals(ysf)) {
					ys(list, "/");
				} else if ("*".equals(ysf)) {
					ys(list, "*");
				} else if ("-".equals(ysf)) {
					ys(list, "-");
				} else if ("+".equals(ysf)) {
					ys(list, "+");
				}

			}
		}

		// for (int i = 0; i < list.size(); i++) {
		// Logger.d("for List is:" + list.get(i) + "size is:" + list.size());
		// }
	}

	private double qys(List<String> list, String ysf, int i) throws MyException {
		String s1 = "";
		String s2 = "";
		String s3 = "";
		String s4 = "";
		int i1 = 0;
		double d1 = 0;
		double d2 = 0;
		double r = 0;

		while (i < list.size()) {
			String x = list.get(i);
			if (x.equals(ysf)) {
				if (i > 0 && "^".equals(x)) {
					// 给方的S1赋�??
					s1 = list.get(i - 1);
					d1 = parseDouble(s1);
				}
				if (i > 0 && "!".equals(x)) {
					// 给阶乘的S4赋�??
					s4 = list.get(i - 1);
					i1 = parseInt(s4);
				}
				if (i < list.size() - 1 && !x.equals("!")) {
					// 避免阶乘时S2空指针异�?
					// Logger.d("list size is:" + list.size() + " i is:" + i);
					s2 = list.get(i + 1);
				}

				if (i + 2 < list.size()) {
					// 赋�?�S3,以检查运算符后边是否还有阶乘或�?�方
					s3 = list.get(i + 2);
				}
				if ("^".equals(s3)) {
					// 运算�?
					d2 = qys(list, "^", i + 1);
				} else if ("!".equals(s3)) {
					// 运算阶乘
					d2 = qys(list, "!", i + 1);
				} else if ("l".equals(s2)) {
					// 如果S2不是数字而是运算�?,则调用自身并给S2赋�??
					d2 = qys(list, "l", i + 1);
				} else if ("n".equals(s2)) {
					d2 = qys(list, "n", i + 1);
				} else if ("√".equals(s2)) {
					d2 = qys(list, "√", i + 1);
				} else if ("s".equals(s2)) {
					d2 = qys(list, "s", i + 1);
				} else if ("c".equals(s2)) {
					d2 = qys(list, "c", i + 1);
				} else if ("t".equals(s2)) {
					d2 = qys(list, "t", i + 1);
				} else if ("o".equals(s2)) {
					d2 = qys(list, "o", i + 1);
				} else if (i < list.size() - 1 && !x.equals("!")) {
					d2 = parseDouble(s2);
				}

				if ("l".equals(ysf)) {
					r = Math.log10(d2);
				} else if ("^".equals(ysf)) {
					r = Math.pow(d1, d2);
				} else if ("!".equals(ysf)) {
					r = diguiJiecheng(i1);
				} else if ("n".equals(ysf)) {
					r = Math.log(d2);
				} else if ("√".equals(ysf)) {
					r = Math.sqrt(d2);
				} else if ("s".equals(ysf)) {
					r = Math.sin((d2 * Math.PI) / 180);
				} else if ("c".equals(ysf)) {
					r = Math.cos(d2 * Math.PI / 180);
				} else if ("t".equals(ysf)) {
					if (90 == Math.abs(d2) % 180) {
						throw new MyException("∞or-∞");
					}
					r = Math.tan(d2 * Math.PI / 180);
				} else if ("o".equals(ysf)) {
					if (0 == Math.abs(d2) % 180) {
						throw new MyException("∞or-∞");
					}
					r = 1 / Math.tan(d2 * Math.PI / 180);
				}

				if ("^".equals(ysf)) {
					list.remove(i - 1);
					list.remove(i - 1);
					list.set(i - 1, String.valueOf(r));
				} else if ("!".equals(ysf)) {
					list.remove(i - 1);
					list.set(i - 1, String.valueOf(r));
				} else {
					list.remove(i);
					list.set(i, String.valueOf(r));
				}

				// �?查是否还有运算符
				if (list.contains("^")) {
					qys(list, "^", i);
				} else if ("√".equals(ysf)) {
					qys(list, "√", i);
				} else if ("l".equals(ysf)) {
					qys(list, "l", i);
				} else if ("n".equals(ysf)) {
					qys(list, "n", i);
				} else if ("s".equals(ysf)) {
					qys(list, "s", i);
				} else if ("c".equals(ysf)) {
					qys(list, "c", i);
				} else if ("t".equals(ysf)) {
					qys(list, "t", i);
				} else if ("o".equals(ysf)) {
					qys(list, "o", i);
				} else if ("!".equals(ysf)) {
					qys(list, "!", i);
				}
				//
				if (String.valueOf(r).contains(".")
						&& String.valueOf(r)
								.substring(String.valueOf(r).length() - 1)
								.equals("0")
						&& !String.valueOf(r).contains("E")) {
					int r1 = (int) r;
					result.setText(String.valueOf(r1));
					// Logger.d("执行�?" + r1);
					return r;

				} else {
					// BigDecimal b = new BigDecimal(r);
					NumberFormat nf = NumberFormat.getNumberInstance();
					nf.setMaximumFractionDigits(8);
					result.setText(String.valueOf(nf.format(r)));
					return r;

				}

			}

			i++;
		}

		return r;

		// for (int i = 0; i < list.size(); i++) {
		// Logger.d("for List is:" + list.get(i) + "size is:" + list.size());
		// }
	}

	private int diguiJiecheng(int num) {
		if (num > 0)
			return num * diguiJiecheng(num - 1);
		else {
			return 1;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.number_0:
		case R.id.number_1:
		case R.id.number_2:
		case R.id.number_3:
		case R.id.number_4:
		case R.id.number_5:
		case R.id.number_6:
		case R.id.number_7:
		case R.id.number_8:
		case R.id.number_9:
		case R.id.plus:
		case R.id.devide:
		case R.id.Multiply:
		case R.id.minus:
		case R.id.l_bracket:
		case R.id.r_bracket:
		case R.id.pai:
		case R.id.e:
		case R.id.ln:
		case R.id.log:
		case R.id.jiecheng:
		case R.id.kaifang:
		case R.id.genhao:
		case R.id.sin:
		case R.id.cos:
		case R.id.tan:
		case R.id.cot:
		case R.id.del:
		case R.id.ans:

			setText(v);
			break;
		case R.id.equal:
			// TODO
			for (xPosition = 0; xPosition < XList.length; xPosition++) {
				try {
					getresult(equaltion.getText().toString(), xPosition);
				} catch (NumberFormatException e) {
					if ("Infinity or NaN: Infinity".equals(e.getMessage())) {
						// result.setText("∞");
						// 当为无穷大时 添加这个 在draw的时候判断 不绘制
						result.setText("");
						// specialPoint = xPosition;
						specialPoint.add(xPosition);
						// Toast.makeText(context, "∞", 0).show();
					} else {
						// result.setText("error!!!");
						// Toast.makeText(context, "格式问题!", 0).show();
					}

				} catch (IndexOutOfBoundsException e) {
					// result.setText("error!!!");
					Toast.makeText(context, "越界异常!", 0).show();
				} catch (MyException e) {
					if ("- before sin".equals(e.getMessage())) {
						// result.setText("实现这个有点困难,而sin图像是交叉对称的�?以sin-x=-sinx.将就点吧.");
						Toast.makeText(context,
								"实现这个有点困难,而sin图像是交叉对称的�?以sin-x=-sinx.将就点吧.", 0)
								.show();
					} else if ("- before cos".equals(e.getMessage())) {
						// result.setText("实现这个有点困难,而cos图像是对称的�?以cos-x=cosx.将就点吧.");
						Toast.makeText(context,
								"实现这个有点困难,而cos图像是对称的�?以cos-x=cosx.将就点吧.", 0)
								.show();
					} else if ("- before tan".equals(e.getMessage())) {
						// result.setText("实现这个有点困难,而tan图像是交叉对称的�?以tan-x=-tanx.将就点吧.");
						Toast.makeText(context,
								"实现这个有点困难,而tan图像是交叉对称的�?以tan-x=-tanx.将就点吧.", 0)
								.show();
					} else if ("- before cot".equals(e.getMessage())) {
						// result.setText("实现这个有点困难,而cot图像是交叉对称的�?以cot-x=-cotx.将就点吧.");
						Toast.makeText(context,
								"实现这个有点困难,而cot图像是交叉对称的�?以cot-x=-cotx.将就点吧.", 0)
								.show();
					}
				}
				if (!TextUtils.isEmpty(result.getText().toString())) {
					// 数值大了会有逗号 去掉就好
					String spf = result.getText().toString();
					spf = spf.replaceAll(",", "");
					PointF pf = new PointF(XList[xPosition], Float.valueOf(spf));
					XYList.add(pf);
					Log.d("jisuan", "计算结果是:" + pf.x + "," + pf.y);
				}
			}
			if (null != onCalculatorResultListener && XYList.size() > 0) {
				String formula = equaltion.getText().toString();
				onCalculatorResultListener.onCalculatorResult(formula, XYList,
						specialPoint);
			}
			break;
		case R.id.c:
			equaltion.setText("");
			tempStr = "";
			result.setText("");
			break;
		case R.id.delete:
			if (TextUtils.isEmpty(tempStr)) {
				return;
			}
			if (TextUtils.isEmpty(result.getText().toString())) {
				StringBuilder sb = new StringBuilder(tempStr);
				sb.delete(tempStr.length() - 1, tempStr.length());
				tempStr = sb.toString();
				equaltion.setText(sb);
			} else {
				tempStr = "";
				equaltion.setText("");
				result.setText("");
			}
			break;

		default:
			break;
		}

	}

	private double parseDouble(String dStr) {
		if (null == dStr || "".equals(dStr))
			return 0d;// 应该写出错了
		return Double.parseDouble(dStr);
	}

	private int parseInt(String dStr) {
		if (null == dStr || "".equals(dStr))
			return 0;// 应该写出错了
		return Integer.parseInt(dStr);
	}

	/**
	 * 判断是否是四则运算符
	 */
	private boolean verifyOperator(String operator) {
		if (operator.equals("+") || operator.equals("-")
				|| operator.equals("*") || operator.equals("/")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是整数
	 */
	private boolean verifyCanToInt(String num) {
		if (num.contains(".") && num.substring(num.length() - 1).equals("0")
				&& !num.contains("E")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是合法的数字,只存在一个小数点,并且不能再第0位和�?后一�?
	 * 
	 * @param string
	 * @return
	 */
	private boolean verifyNumber(String string) {
		StringBuilder sb = new StringBuilder(string);
		int n = 0;
		int n1 = 0;
		for (int i = 0; i < sb.length(); i++) {
			char s = sb.charAt(i);
			if (s == '.') {
				n++;
			} else if (s == '-') {
				n1++;
			}
		}
		if (n1 == 0) {
			if (n == 0) {
				return true;
			} else if (n == 1) {
				if (sb.charAt(0) != '.' && sb.charAt(sb.length() - 1) != '.') {
					return true;
				}
			} else {
				return false;
			}
		} else if (n1 == 1 && sb.charAt(0) == '-') {
			if (n == 0) {
				return true;
			} else if (n == 1) {
				if (sb.charAt(1) != '.' && sb.charAt(sb.length() - 1) != '.') {
					return true;
				}
			} else {
				return false;
			}
		}
		return false;
	}

	public void setOnCalculatorResultListener(
			OnCalculatorResultListener onCalculatorResultListener) {
		this.onCalculatorResultListener = onCalculatorResultListener;
	}

	public interface OnCalculatorResultListener {
		public void onCalculatorResult(String formula, ArrayList<PointF> res,
									   ArrayList<Integer> specialPoint);
	}
}
