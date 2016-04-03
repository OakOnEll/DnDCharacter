package com.oakonell.dndcharacter.model.character;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.utils.NumberUtils;

import org.simpleframework.xml.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rob on 4/3/2016.
 */
public class MoneyValue implements Parcelable {
    // Method to recreate a HpRow from a Parcel
    @NonNull
    public static Creator<MoneyValue> CREATOR = new Creator<MoneyValue>() {

        @NonNull
        @Override
        public MoneyValue createFromParcel(@NonNull Parcel source) {
            return new MoneyValue(source);
        }

        @NonNull
        @Override
        public MoneyValue[] newArray(int size) {
            return new MoneyValue[size];
        }

    };

    public MoneyValue(int platinum, int gold, int electrum, int silver, int copper) {
        this.platinum = platinum;
        this.gold = gold;
        this.electrum = electrum;
        this.silver = silver;
        this.copper = copper;
    }

    public MoneyValue() {
        this.platinum = 0;
        this.gold = 0;
        this.electrum = 0;
        this.silver = 0;
        this.copper = 0;
    }

    @Element(required = false)
    private int platinum;
    @Element(required = false)
    private int gold;
    @Element(required = false)
    private int electrum;
    @Element(required = false)
    private int silver;
    @Element(required = false)
    private int copper;


    public MoneyValue add(MoneyValue value) {
        return new MoneyValue(platinum + value.platinum, gold + value.gold, electrum + value.electrum, silver + value.silver, copper + value.copper);
    }

    public MoneyValue simplified() {
        int workCopper = copper;
        int workSilver = silver;
        int workElectrum = electrum;
        int workGold = gold;
        int workPlatinum = platinum;

        int newCopper = workCopper % 10;
        workSilver += workCopper / 10;

        int newSilver = workSilver % 5;
        workElectrum += workSilver / 5;

        int newElectrum = workElectrum % 2;
        workGold += workElectrum / 2;

        int newGold = workGold % 10;
        workPlatinum += workGold / 10;

        int newPlatinum = workPlatinum;

        return new MoneyValue(newPlatinum, newGold, newElectrum, newSilver, newCopper);
    }

    private static final Pattern coinPattern = Pattern.compile("\\G((?:0|[1-9][0-9]{0,2}(?:,[0-9]{3})*))\\s*([pgesc]p)[,\\s*]?");

    public static MoneyValue fromString(Context context, String string) throws InvalidMoneyFormatException {
        String input = string.trim();
        final Matcher matcher = coinPattern.matcher(input);
        int platinum = 0;
        int gold = 0;
        int electrum = 0;
        int silver = 0;
        int copper = 0;
        boolean anyMatch = false;
        int lastMatchPos = 0;
        while (matcher.find()) {
            anyMatch = true;
            String numberString = matcher.group(1);
            int number;
            try {
                number = Integer.parseInt(numberString);
            } catch (NumberFormatException e) {
                throw new InvalidMoneyFormatException(context.getString(R.string.invalid_number_format, numberString, input), input);

            }
            String type = matcher.group(2);
            lastMatchPos = matcher.end();
            switch (type) {
                case "pp":
                    platinum = check(context, platinum, number, type, input);
                    break;
                case "gp":
                    gold = check(context, gold, number, type, input);
                    break;
                case "ep":
                    electrum = check(context, electrum, number, type, input);
                    break;
                case "sp":
                    silver = check(context, silver, number, type, input);
                    break;
                case "cp":
                    copper = check(context, copper, number, type, input);
                    break;
                default:
                    throw new InvalidMoneyFormatException(context.getString(R.string.unexpected_coin_type, type, input), input);
            }
        }
        if (lastMatchPos != input.length() || !anyMatch) {
            throw new InvalidMoneyFormatException(context.getString(R.string.invalid_money_value_format, input), input);
        }
        return new MoneyValue(platinum, gold, electrum, silver, copper);
    }

    private static int check(Context context, int existingValue, int number, String type, String input) throws InvalidMoneyFormatException {
        if (existingValue > 0) {
            throw new InvalidMoneyFormatException(context.getString(R.string.duplicate_coin_type, type, input), input);
        }
        return number;
    }

    public String asFormattedString() {
        StringBuilder builder = new StringBuilder();
        boolean placeComma = false;
        if (platinum > 0) {
            builder.append(NumberUtils.formatNumber(platinum));
            builder.append("pp");
            placeComma = true;
        }
        if (gold > 0) {
            if (placeComma) builder.append(",");
            builder.append(NumberUtils.formatNumber(gold));
            builder.append("gp");
            placeComma = true;
        }
        if (electrum > 0) {
            if (placeComma) builder.append(",");
            builder.append(NumberUtils.formatNumber(electrum));
            builder.append("ep");
            placeComma = true;
        }
        if (silver > 0) {
            if (placeComma) builder.append(",");
            builder.append(NumberUtils.formatNumber(silver));
            builder.append("sp");
            placeComma = true;
        }
        if (copper > 0) {
            if (placeComma) builder.append(",");
            builder.append(NumberUtils.formatNumber(copper));
            builder.append("cp");
        }

        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public MoneyValue(Parcel source) {
        platinum = source.readInt();
        gold = source.readInt();
        electrum = source.readInt();
        silver = source.readInt();
        copper = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(platinum);
        dest.writeInt(gold);
        dest.writeInt(electrum);
        dest.writeInt(silver);
        dest.writeInt(copper);
    }

    public static class InvalidMoneyFormatException extends Exception {
        private final String input;

        public InvalidMoneyFormatException(String error, String input) {
            super(error);
            this.input = input;
        }
    }
}
