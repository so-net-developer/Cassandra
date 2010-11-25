Name: zookeeper
Version: 3.3.1
Release: 0
Group: Program
Source: zookeeper-3.3.1.tar.gz
Summary: zookeeper-base
License: so-net
BuildRoot: /var/tmp/zookeeper
BuildArch: noarch

%description
ApacheZookeeper


%prep
%setup

/bin/rm -rf $RPM_BUILD_ROOT/usr/lib/zookeeper-3.3.1
/bin/rm -rf $RPM_BUILD_ROOT/etc/init.d
/bin/rm -rf $RPM_BUILD_ROOT/etc
/bin/mkdir -p $RPM_BUILD_ROOT/usr/lib/zookeeper-3.3.1
/bin/mkdir -p $RPM_BUILD_ROOT/etc
/bin/mkdir -p $RPM_BUILD_ROOT/etc/init.d


%install
/bin/cp -R zookeeper-3.3.1 $RPM_BUILD_ROOT/usr/lib/
/bin/cp etc/init.d/zookeeper $RPM_BUILD_ROOT/etc/init.d/


%post
/usr/sbin/adduser zookeeper
/bin/mkdir /var/zookeeper
/bin/chown -R zookeeper:zookeeper /var/zookeeper
/bin/mkdir /var/log/zookeeper
/bin/chown -R zookeeper:zookeeper /var/log/zookeeper

%clean
/bin/rm -rf $RPM_BUILD_ROOT

%preun
/bin/rm -rf /var/zookeeper
/usr/sbin/userdel zookeeper

%files
/usr/lib/zookeeper-3.3.1
/etc/init.d/zookeeper
