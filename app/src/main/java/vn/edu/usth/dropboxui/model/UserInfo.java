package vn.edu.usth.dropboxui.model;

public class UserInfo {
    private String account_id;
    private AccountType account_type;
    private String country;
    private boolean disabled;
    private String email;
    private boolean email_verified;
    private boolean is_paired;
    private String locale;
    private Name name;
    private String profile_photo_url;
    private String referral_link;
    private RootInfo root_info;

    public Name getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountId() {
        return account_id;
    }

    public AccountType getAccountType() {
        return account_type;
    }

    public String getCountry() {
        return country;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isEmailVerified() {
        return email_verified;
    }

    public boolean isPaired() {
        return is_paired;
    }

    public String getLocale() {
        return locale;
    }

    public String getProfilePhotoUrl() {
        return profile_photo_url;
    }

    public String getReferralLink() {
        return referral_link;
    }

    public RootInfo getRootInfo() {
        return root_info;
    }


    public static class AccountType {
        private String tag;

    }

    public static class Name {
        private String abbreviated_name;
        private String display_name;
        private String familiar_name;
        private String given_name;
        private String surname;

        public String getDisplayName() {
            return display_name;
        }

    }

    public static class RootInfo {
        private String tag;
        private String home_namespace_id;
        private String root_namespace_id;

    }
}