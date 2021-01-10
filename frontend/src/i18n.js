import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { register } from 'timeago.js';

i18n.use(initReactI18next).init({
    resources: {
        en: {
            translations: {
                'Sign Up': 'Sign Up',
                'Password Mismatch': 'Passwords do not macth!',
                'Username': 'Username',
                'Display Name': 'Display Name',
                'Password': 'Password',
                'Confirm Password': 'Confirm Password',
                'Login': 'Login',
                'Logout': 'Logout',
                'Users': 'Users',
                'Next': 'Next',
                'Previous': 'Previous',
                'Load Failure': 'Load Failure',
                'User Not Found': 'User Not Found!',
                'Edit': 'Edit',
                'Save': 'Save',
                'Cancel': 'Cancel',
                'Change Display Name': 'Change Display Name',
                'My Profile': 'My Profile',
                'No Content': 'There are not shared Contents',
                'Share Thoughts': 'Share Thoughts',
                'Load Past Contents' : 'Load Past Shared Thoughts',
                'There are new contents': 'There are new contents',
                'Content has unknown attachment!': 'Content has unknown attachment!',
                'Delete Post': 'Delete Post',
                'Are you sure to delete following post?': 'Are you sure to delete following post?',
                'Delete My Account': 'Delete My Account',
                'Are you sure you want to delete your account?': 'Are you sure you want to delete your account?',
                'Delete User Header': 'You are about to delete your user :(',
                'Delete User': 'Delete User'
            }
        },
        tr: {
            translations: {
                'Sign Up': 'Kayıt Ol',
                'Password Mismatch': 'Şifreler uyuşmuyor',
                'Username': 'Kullanıcı adı',
                'Display Name': 'Görünür İsim',
                'Password': 'Şifre',
                'Confirm Password': 'Şifre Tekrarı',
                'Login': 'Giriş Yap',
                'Logout': 'Çıkış Yap',
                'Users': 'Kullanıcılar',
                'Next': 'Sonraki',
                'Previous': 'Önceki',
                'Load Failure': 'Kullanıcılar yüklenemiyor',
                'User Not Found': 'Kullanıcı Bulunamadı!',
                'Edit': 'Düzenle',
                'Save': 'Kaydet',
                'Cancel': 'İptal',
                'Change Display Name': 'Görünür İsmi Değiştir',
                'My Profile': 'Profilim',
                'No Content': 'Paylaşılmış bir içerik bulunmamaktadır',
                'Share Thoughts': 'Düşüncelerini paylaş',
                'Load Past Contents' : 'Geçmişte yapılmış paylaşımları yükle',
                'There are new contents': 'Yeni gönderiler var',
                'Content has unknown attachment!': 'Bilinmeyen dosya tipi',
                'Delete Post': 'Postu Sil',
                'Are you sure to delete following post?': 'Bu postu silmek istediginizden emin misiniz?',
                'Delete My Account': 'Hesabımı Sil',
                'Are you sure you want to delete your account?': 'Hesabınızı silmek istediğinize emin misiniz?',
                'Delete User Header': 'Hesabınızı silmek üzeresiniz.',
                'Delete User': 'Kullanıcıyı Sil'       
            }
        }
    },
    fallbackLng: 'en',
    ns: ['translations'],
    defaultNS: 'translations',
    keySeparator: false,
    interpolation: {
        escapeValue: false,
        formatSeperator: ','
    },
    react: {
        wait: true
    }
});

const timeAgoTr = (number, index) => {
    return [
      ['az önce', 'şimdi'],
      ['%s saniye önce', '%s saniye içinde'],
      ['1 dakika önce', '1 dakika içinde'],
      ['%s dakika önce', '%s dakika içinde'],
      ['1 saat önce', '1 saat içinde'],
      ['%s saat önce', '%s saat içinde'],
      ['1 gün önce', '1 gün içinde'],
      ['%s gün önce', '%s gün içinde'],
      ['1 hafta önce', '1 hafta içinde'],
      ['%s hafta önce', '%s hafta içinde'],
      ['1 ay önce', '1 ay içinde'],
      ['%s ay önce', '%s ay içinde'],
      ['1 yıl önce', '1 yıl içinde'],
      ['%s yıl önce', '%s yıl içinde'],
    ][index];
}

register('tr', timeAgoTr);

export default i18n;