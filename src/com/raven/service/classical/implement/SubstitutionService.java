package com.raven.service.classical.implement;

import com.raven.service.classical.IClassicalService;

public class SubstitutionService implements IClassicalService {

    private static final String ALPHABET_VIETNAM = "aáàảãạâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyýỳỷỹỵz";
    private static final int SHIFT = 3;

    @Override
    public boolean requireKey() {
        return false;
    }

    @Override
    public String generateKey(int length) {
        return "";
    }

    @Override
    public String decrypt(String inputText) {
        return shiftText(inputText, -SHIFT);
    }

    @Override
    public String encrypt(String inputText) {
        return shiftText(inputText, SHIFT);
    }


    private String shiftText(String inputText, int shift) {
        StringBuilder outputText = new StringBuilder();
        for (char c : inputText.toCharArray()) {
            if (isVietnameseLetter(c)) {
                char shiftedChar = shiftCharacter(c, shift);
                outputText.append(shiftedChar);
            } else {
                outputText.append(c);
            }
        }
        return outputText.toString();
    }

    private boolean isVietnameseLetter(char c) {
        return ALPHABET_VIETNAM.indexOf(c) >= 0;
    }


    private char shiftCharacter(char c, int shift) {
        int index = ALPHABET_VIETNAM.indexOf(c);
        int newIndex = (index + shift + ALPHABET_VIETNAM.length()) % ALPHABET_VIETNAM.length();
        return ALPHABET_VIETNAM.charAt(newIndex);
    }

    public static void main(String[] args) {
        SubstitutionService substitutionService = new SubstitutionService();
        String originalText = "Chào bạn, tôi tên là Trần Võ Hoàng Huy lớp ĐH21ĐTB!";
        System.out.println("Văn bản gốc: " + originalText);


        String encryptedText = substitutionService.encrypt(originalText);
        System.out.println("Văn bản sau khi mã hóa: " + encryptedText);


        String decryptedText = substitutionService.decrypt(encryptedText);
        System.out.println("Văn bản sau khi giải mã: " + decryptedText);
    }
}
