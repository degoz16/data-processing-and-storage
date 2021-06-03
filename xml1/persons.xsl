<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:output method="html"/>
  <xsl:template match="/*[local-name()='persons']">
    <html>
      <body>
        <xsl:for-each select="person">
          <xsl:if test="mother and father
                    and id(mother/@person-id)/mother and id(mother/@person-id)/father
                    and id(father/@person-id)/mother and id(father/@person-id)/father
                    and sisters and brothers">
            <table border="1">
              <tr>
                <th>Person Name</th>
                <th>Gender</th>
                <th>Mother</th>
                <th>Father</th>
                <th>Brothers</th>
                <th>Sisters</th>
                <th>Sons</th>
                <th>Daughters</th>
                <th>Grand-Mothers</th>
                <th>Grand-Fathers</th>
                <th>Uncles</th>
                <th>Aunts</th>
              </tr>
              <xsl:apply-templates select="."/>
              <xsl:apply-templates select="id(mother/@person-id)"/>
              <xsl:apply-templates select="id(father/@person-id)"/>
              <xsl:for-each select="id(brothers/brother/@person-id)">
                <xsl:apply-templates select="."/>
              </xsl:for-each>
              <xsl:for-each select="id(sisters/sister/@person-id)">
                <xsl:apply-templates select="."/>
              </xsl:for-each>
            </table>
            <br/>
            <br/>
            <br/>
          </xsl:if>
        </xsl:for-each>
      </body>
    </html>
  </xsl:template>


  <xsl:template match="person">

    <tr>
      <td>
        <xsl:value-of select="concat(@firstname, ' ', @surname)"/>
      </td>
      <td>
        <xsl:value-of select="gender"/>
      </td>
      <td>
        <xsl:variable name="mom" select="id(mother/@person-id)"/>
        <xsl:value-of
          select="concat($mom/@firstname, ' ', $mom/@surname)"/>
      </td>
      <td>
        <xsl:variable name="fa" select="id(father/@person-id)"/>
        <xsl:value-of
          select="concat($fa/@firstname, ' ', $fa/@surname)"/>
      </td>
      <td>
        <xsl:for-each select="brothers/brother/@person-id">
          <xsl:variable name="bro" select="id(.)"/>
          <xsl:value-of
            select="concat($bro/@firstname, ' ', $bro/@surname)"/>
          <xsl:if test="position() != last()">
            <xsl:text>,</xsl:text>
          </xsl:if>
        </xsl:for-each>
      </td>
      <td>
        <xsl:for-each select="sisters/sister/@person-id">
          <xsl:variable name="sis" select="id(.)"/>
          <xsl:value-of
            select="concat($sis/@firstname, ' ', $sis/@surname)"/>
          <xsl:if test="position() != last()">
            <xsl:text>,</xsl:text>
          </xsl:if>
        </xsl:for-each>
      </td>
      <td>
        <xsl:if test="sons">
          <xsl:for-each select="sons/son/@person-id">
            <xsl:variable name="son" select="id(.)"/>
            <xsl:value-of
              select="concat($son/@firstname, ' ', $son/@surname)"/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
      </td>
      <td>
        <xsl:if test="daughters">
          <xsl:for-each select="daughters/daughter/@person-id">
            <xsl:variable name="daughter" select="id(.)"/>
            <xsl:value-of
              select="concat($daughter/@firstname, ' ', $daughter/@surname)"/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
      </td>
      <td>
        <xsl:variable name="grand-momM" select="id(id(mother/@person-id)/mother/@person-id)"/>
        <xsl:variable name="grand-momF" select="id(id(father/@person-id)/mother/@person-id)"/>
        <xsl:value-of
          select="concat($grand-momM/@firstname, ' ', $grand-momM/@surname)"/>
        <xsl:text>,</xsl:text>
        <xsl:value-of
          select="concat($grand-momF/@firstname, ' ', $grand-momF/@surname)"/>
      </td>
      <td>
        <xsl:variable name="grand-faM" select="id(id(mother/@person-id)/father/@person-id)"/>
        <xsl:variable name="grand-faF" select="id(id(father/@person-id)/father/@person-id)"/>
        <xsl:value-of
          select="concat($grand-faM/@firstname, ' ', $grand-faM/@surname)"/>
        <xsl:text>,</xsl:text>
        <xsl:value-of
          select="concat($grand-faF/@firstname, ' ', $grand-faF/@surname)"/>
      </td>
      <td>
        <xsl:if test="id(father/@person-id)/brothers">
          <xsl:for-each select="id(father/@person-id)/brothers/brother/@person-id">
            <xsl:variable name="uncle" select="id(.)"/>
            <xsl:value-of
              select="concat($uncle/@firstname, ' ', $uncle/@surname)"/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
        <xsl:if test="id(mother/@person-id)/brothers">
          <xsl:if test="id(father/@person-id)/brothers">
            <xsl:text>,</xsl:text>
          </xsl:if>
          <xsl:for-each select="id(mother/@person-id)/brothers/brother/@person-id">
            <xsl:variable name="uncle" select="id(.)"/>
            <xsl:value-of
              select="concat($uncle/@firstname, ' ', $uncle/@surname)"/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
      </td>
      <td>
        <xsl:if test="id(father/@person-id)/sisters">
          <xsl:for-each select="id(father/@person-id)/sisters/sister/@person-id">
            <xsl:variable name="aunt" select="id(.)"/>
            <xsl:value-of
              select="concat($aunt/@firstname, ' ', $aunt/@surname)"/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
        <xsl:if test="id(mother/@person-id)/sisters">
          <xsl:if test="id(father/@person-id)/sisters">
            <xsl:text>,</xsl:text>
          </xsl:if>
          <xsl:for-each select="id(mother/@person-id)/sisters/sister/@person-id">
            <xsl:variable name="aunt" select="id(.)"/>
            <xsl:value-of
              select="concat($aunt/@firstname, ' ', $aunt/@surname)"/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
