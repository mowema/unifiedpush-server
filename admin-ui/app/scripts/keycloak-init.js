'use strict';

(function () {
  /**
   * Snippet extracted from Keycloak examples
   */
  var auth = {};
  var app = angular.module('upsConsole');

  angular.element(document).ready(function () {
    var keycloak = new Keycloak('rest/keycloak/config');
    auth.loggedIn = false;

    keycloak.init({ onLoad: 'login-required' }).success(function () {
      auth.loggedIn = true;
      auth.keycloak = keycloak;
      auth.logout = function() {
        auth.loggedIn = false;
        auth.keycloak.logout();
        auth.keycloak = null;
        window.location = keycloak.createLogoutUrl();
      };
      app.factory('Auth', function () {
        return auth;
      });
      angular.bootstrap(document, ['upsConsole']);
    }).error(function () {
      window.location.reload();
    });

  });

  app.factory('Auth', function () {
    return auth;
  });

  app.factory('authInterceptor', function ($q, Auth) {
    return {
      request: function (config) {
        var deferred = $q.defer();

        if (config.url === 'rest/sender' || config.url === 'rest/registry/device/importer') {
          return config;
        }

        if (Auth.keycloak && Auth.keycloak.token) {
          Auth.keycloak.updateToken(5).success(function () {
            config.headers = config.headers || {};
            config.headers.Authorization = 'Bearer ' + Auth.keycloak.token;

            deferred.resolve(config);
          }).error(function () {
            window.location.reload();
          });
        }
        return deferred.promise;
      }
    };
  });

  app.config(function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
  });

})();
